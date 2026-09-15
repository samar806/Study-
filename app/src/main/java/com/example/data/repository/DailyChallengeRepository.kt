package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.data.model.GeneratedMcq
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayStreakMarkerData(
    val dateString: String,
    val dayLabel: String,
    val dayNumberStr: String,
    val isCompleted: Boolean,
    val isToday: Boolean
)

data class DailyChallengeData(
    val dateString: String,
    val questions: List<GeneratedMcq>,
    val userAnswers: Map<Int, Int>, // question index -> option index
    val isCompleted: Boolean,
    val score: Int,
    val lastAnsweredIndex: Int
)

object DailyChallengeRepository {
    private const val PREFS_NAME = "daily_challenge_prefs"
    private const val KEY_COMPLETED_DATES = "completed_dates_set"
    private const val KEY_CHALLENGE_DATE = "challenge_date"
    private const val KEY_QUESTIONS_JSON = "challenge_questions_json"
    private const val KEY_ANSWERS_JSON = "challenge_answers_json"
    private const val KEY_IS_COMPLETED = "challenge_is_completed"
    private const val KEY_SCORE = "challenge_score"
    private const val KEY_LAST_INDEX = "challenge_last_index"

    private var prefs: SharedPreferences? = null

    // Compose observable state for live UI updates
    var currentChallengeState by mutableStateOf<DailyChallengeData?>(null)
        private set

    var completedDatesHistory by mutableStateOf<Set<String>>(emptySet())
        private set

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadCompletedDates()
            ensureTodayChallengeLoaded()
        }
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    private fun loadCompletedDates() {
        val prefs = prefs ?: return
        val set = prefs.getStringSet(KEY_COMPLETED_DATES, null)
        if (set != null) {
            completedDatesHistory = set.toSet()
        } else {
            // Seed previous 4 days (days -4, -3, -2, -1) so initial user starts with active streak
            val seedSet = mutableSetOf<String>()
            val cal = Calendar.getInstance()
            for (offset in 1..4) {
                cal.time = Date()
                cal.add(Calendar.DAY_OF_YEAR, -offset)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                seedSet.add(sdf.format(cal.time))
            }
            completedDatesHistory = seedSet
            saveCompletedDates(seedSet)
        }
    }

    private fun saveCompletedDates(dates: Set<String>) {
        prefs?.edit()?.putStringSet(KEY_COMPLETED_DATES, dates)?.apply()
    }

    fun ensureTodayChallengeLoaded(
        userExam: String = "NEET/JEE",
        userGrade: String = "Class 12",
        userSubjects: List<String> = listOf("Physics", "Chemistry")
    ) {
        val todayStr = getTodayDateString()
        val prefs = prefs ?: return

        val savedDate = prefs.getString(KEY_CHALLENGE_DATE, null)
        if (savedDate == todayStr) {
            // Load existing challenge for today
            val qJsonStr = prefs.getString(KEY_QUESTIONS_JSON, null)
            val questions = if (!qJsonStr.isNullOrEmpty()) parseQuestionsJson(qJsonStr) else generateDailyQuestions(userExam, userGrade, userSubjects)

            val aJsonStr = prefs.getString(KEY_ANSWERS_JSON, null)
            val answers = if (!aJsonStr.isNullOrEmpty()) parseAnswersJson(aJsonStr) else emptyMap()

            val isCompleted = prefs.getBoolean(KEY_IS_COMPLETED, false)
            val score = prefs.getInt(KEY_SCORE, 0)
            val lastIndex = prefs.getInt(KEY_LAST_INDEX, 0)

            currentChallengeState = DailyChallengeData(
                dateString = todayStr,
                questions = questions,
                userAnswers = answers,
                isCompleted = isCompleted,
                score = score,
                lastAnsweredIndex = lastIndex
            )
        } else {
            // Midnight reset or new day: generate fresh set for today!
            val newQuestions = generateDailyQuestions(userExam, userGrade, userSubjects)
            val newState = DailyChallengeData(
                dateString = todayStr,
                questions = newQuestions,
                userAnswers = emptyMap(),
                isCompleted = false,
                score = 0,
                lastAnsweredIndex = 0
            )
            currentChallengeState = newState
            saveStateToPrefs(newState)
        }
    }

    private fun saveStateToPrefs(state: DailyChallengeData) {
        val prefs = prefs ?: return
        prefs.edit().apply {
            putString(KEY_CHALLENGE_DATE, state.dateString)
            putString(KEY_QUESTIONS_JSON, encodeQuestionsJson(state.questions))
            putString(KEY_ANSWERS_JSON, encodeAnswersJson(state.userAnswers))
            putBoolean(KEY_IS_COMPLETED, state.isCompleted)
            putInt(KEY_SCORE, state.score)
            putInt(KEY_LAST_INDEX, state.lastAnsweredIndex)
            apply()
        }
    }

    fun saveAnswer(questionIndex: Int, selectedOption: Int) {
        val state = currentChallengeState ?: return
        if (state.isCompleted) return // Read-only if completed today

        val newAnswers = state.userAnswers.toMutableMap()
        newAnswers[questionIndex] = selectedOption
        val newLastIndex = maxOf(state.lastAnsweredIndex, questionIndex)

        val updatedState = state.copy(
            userAnswers = newAnswers,
            lastAnsweredIndex = newLastIndex
        )
        currentChallengeState = updatedState
        saveStateToPrefs(updatedState)
    }

    fun completeChallenge(): Int {
        val state = currentChallengeState ?: return 0
        val todayStr = getTodayDateString()

        // Calculate score
        var correctCount = 0
        state.questions.forEachIndexed { idx, q ->
            val userOpt = state.userAnswers[idx]
            if (userOpt != null && userOpt == q.correctAnswerIndex) {
                correctCount++
            }
        }

        val updatedState = state.copy(
            isCompleted = true,
            score = correctCount
        )
        currentChallengeState = updatedState
        saveStateToPrefs(updatedState)

        // Add today to completed dates
        val newHistory = completedDatesHistory.toMutableSet()
        newHistory.add(todayStr)
        completedDatesHistory = newHistory
        saveCompletedDates(newHistory)

        return correctCount
    }

    fun getCurrentStreak(): Int {
        val todayStr = getTodayDateString()
        val todayCompleted = completedDatesHistory.contains(todayStr)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()

        var streak = 0
        if (todayCompleted) {
            cal.time = Date()
        } else {
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            val dateStr = sdf.format(cal.time)
            if (completedDatesHistory.contains(dateStr)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        return streak
    }

    fun getLast7CalendarDays(): List<DayStreakMarkerData> {
        val result = mutableListOf<DayStreakMarkerData>()
        val todayStr = getTodayDateString()

        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val sdfDayLabel = SimpleDateFormat("EEE", Locale.US)
        val sdfDayNum = SimpleDateFormat("d", Locale.US)

        for (i in -6..0) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, i)

            val dateStr = sdfDate.format(cal.time)
            val dayLabel = sdfDayLabel.format(cal.time)
            val dayNum = sdfDayNum.format(cal.time)
            val isCompleted = completedDatesHistory.contains(dateStr)
            val isToday = (dateStr == todayStr)

            result.add(
                DayStreakMarkerData(
                    dateString = dateStr,
                    dayLabel = dayLabel,
                    dayNumberStr = dayNum,
                    isCompleted = isCompleted,
                    isToday = isToday
                )
            )
        }

        return result
    }

    private fun generateDailyQuestions(userExam: String, userGrade: String, userSubjects: List<String>): List<GeneratedMcq> {
        return listOf(
            GeneratedMcq(
                question = "A particle moves in a straight line with uniform acceleration. If it covers 24 m in the 3rd second and 40 m in the 5th second, what is its acceleration?",
                options = listOf("A. 4 m/s²", "B. 8 m/s²", "C. 6 m/s²", "D. 2 m/s²"),
                correctAnswerIndex = 1,
                explanation = "S_n = u + a(n - 1/2). S_3 = u + 2.5a = 24. S_5 = u + 4.5a = 40. Subtracting gives 2a = 16 => a = 8 m/s².",
                difficulty = "Medium",
                subject = "Physics",
                topic = "Kinematics & Motion"
            ),
            GeneratedMcq(
                question = "For a first-order reaction, the half-life period (t1/2) is 20 minutes. How much time is required for 75% completion of the reaction?",
                options = listOf("A. 30 minutes", "B. 40 minutes", "C. 60 minutes", "D. 80 minutes"),
                correctAnswerIndex = 1,
                explanation = "75% completion means 2 half-lives. 2 × 20 minutes = 40 minutes.",
                difficulty = "Medium",
                subject = "Chemistry",
                topic = "Chemical Kinetics"
            ),
            GeneratedMcq(
                question = "Which of the following organic compounds will give a positive Idoform test upon reaction with I₂ and NaOH?",
                options = listOf("A. Methanol", "B. Ethanol", "C. 1-Propanol", "D. Benzophenone"),
                correctAnswerIndex = 1,
                explanation = "Ethanol (CH₃CH₂OH) contains the CH₃CH(OH)- group required for the iodoform reaction to form yellow CHI₃ precipitate.",
                difficulty = "Medium",
                subject = "Chemistry",
                topic = "Alcohols & Aldehydes"
            ),
            GeneratedMcq(
                question = "Two capacitors of capacitance 3 µF and 6 µF are connected in series across a 12 V battery. What is the potential difference across the 3 µF capacitor?",
                options = listOf("A. 4 V", "B. 6 V", "C. 8 V", "D. 12 V"),
                correctAnswerIndex = 2,
                explanation = "Equivalent C_eq = (3×6)/(3+6) = 2 µF. Total charge Q = C_eq × V = 2 µF × 12 V = 24 µC. Voltage across 3 µF is V1 = Q / C1 = 24 / 3 = 8 V.",
                difficulty = "Medium",
                subject = "Physics",
                topic = "Electrostatics & Capacitance"
            ),
            GeneratedMcq(
                question = "During aerobic respiration, how many net ATP molecules are produced per glucose molecule during Glycolysis alone?",
                options = listOf("A. 2 ATP", "B. 4 ATP", "C. 36 ATP", "D. 38 ATP"),
                correctAnswerIndex = 0,
                explanation = "Glycolysis produces 4 ATP molecules by substrate-level phosphorylation but consumes 2 ATP in the investment phase, yielding 2 net ATP.",
                difficulty = "Medium",
                subject = "Biology",
                topic = "Cellular Respiration"
            ),
            GeneratedMcq(
                question = "If A and B are two events such that P(A) = 0.4, P(B) = 0.8, and P(B|A) = 0.6, what is P(A ∪ B)?",
                options = listOf("A. 0.96", "B. 0.84", "C. 0.76", "D. 0.68"),
                correctAnswerIndex = 0,
                explanation = "P(A ∩ B) = P(A) × P(B|A) = 0.4 × 0.6 = 0.24. P(A ∪ B) = P(A) + P(B) - P(A ∩ B) = 0.4 + 0.8 - 0.24 = 0.96.",
                difficulty = "Medium",
                subject = "Mathematics",
                topic = "Probability"
            ),
            GeneratedMcq(
                question = "A ray of light enters a glass prism of refractive index √2 at an angle of incidence 45°. If the ray suffers minimum deviation, what is the angle of the prism?",
                options = listOf("A. 30°", "B. 45°", "C. 60°", "D. 90°"),
                correctAnswerIndex = 2,
                explanation = "By Snell's law, sin(45°)/sin(r) = √2 => sin(r) = 1/2 => r = 30°. At minimum deviation, r = A/2 => A = 2r = 60°.",
                difficulty = "Medium",
                subject = "Physics",
                topic = "Ray Optics"
            ),
            GeneratedMcq(
                question = "Which of the following coordination complex ions exhibits optical isomerism?",
                options = listOf("A. [Co(NH₃)₆]³⁺", "B. cis-[Co(en)₂Cl₂]⁺", "C. trans-[Co(en)₂Cl₂]⁺", "D. [Ni(CN)₄]²⁻"),
                correctAnswerIndex = 1,
                explanation = "cis-[Co(en)₂Cl₂]⁺ lacks a plane of symmetry and forms non-superimposable mirror images (enantiomers), showing optical activity.",
                difficulty = "Medium",
                subject = "Chemistry",
                topic = "Coordination Compounds"
            ),
            GeneratedMcq(
                question = "What is the primary function of the restriction endonuclease enzyme in molecular biology?",
                options = listOf("A. Joining DNA fragments", "B. Cleaving DNA at specific palindrome sequences", "C. Synthesizing RNA primers", "D. Unwinding double-stranded DNA"),
                correctAnswerIndex = 1,
                explanation = "Restriction endonucleases recognize specific palindromic recognition sequences in DNA and cleave the phosphodiester backbone.",
                difficulty = "Medium",
                subject = "Biology",
                topic = "Biotechnology Principles"
            ),
            GeneratedMcq(
                question = "What is the magnitude of magnetic field at the center of a circular loop of radius 10 cm carrying a current of 5 A?",
                options = listOf("A. 3.14 × 10⁻⁵ T", "B. 1.57 × 10⁻⁵ T", "C. 6.28 × 10⁻⁵ T", "D. 3.14 × 10⁻⁴ T"),
                correctAnswerIndex = 0,
                explanation = "B = µ₀ I / (2 R) = (4π × 10⁻7 × 5) / (2 × 0.1) = 10π × 10⁻6 = 3.14 × 10⁻⁵ T.",
                difficulty = "Medium",
                subject = "Physics",
                topic = "Magnetic Effects of Current"
            )
        )
    }

    private fun parseQuestionsJson(jsonStr: String): List<GeneratedMcq> {
        val list = mutableListOf<GeneratedMcq>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val q = obj.getString("q")
                val optsArr = obj.getJSONArray("opts")
                val opts = mutableListOf<String>()
                for (j in 0 until optsArr.length()) {
                    opts.add(optsArr.getString(j))
                }
                val ansIndex = obj.getInt("ans")
                val exp = obj.optString("exp", "")
                val diff = obj.optString("diff", "Medium")
                val sub = obj.optString("sub", "General")
                val top = obj.optString("top", "General")
                list.add(GeneratedMcq(q, opts, ansIndex, exp, diff, sub, top))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun encodeQuestionsJson(questions: List<GeneratedMcq>): String {
        val array = JSONArray()
        for (q in questions) {
            val obj = JSONObject()
            obj.put("q", q.question)
            val optsArr = JSONArray()
            q.options.forEach { optsArr.put(it) }
            obj.put("opts", optsArr)
            obj.put("ans", q.correctAnswerIndex)
            obj.put("exp", q.explanation)
            obj.put("diff", q.difficulty)
            obj.put("sub", q.subject)
            obj.put("top", q.topic)
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseAnswersJson(jsonStr: String): Map<Int, Int> {
        val map = mutableMapOf<Int, Int>()
        try {
            val obj = JSONObject(jsonStr)
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key.toInt()] = obj.getInt(key)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }

    private fun encodeAnswersJson(map: Map<Int, Int>): String {
        val obj = JSONObject()
        map.forEach { (k, v) ->
            obj.put(k.toString(), v)
        }
        return obj.toString()
    }
}
