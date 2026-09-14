package com.example.data.service

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.GeneratedMcq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * High-Speed Multi-AI Parallel MCQ Generation Service.
 *
 * Employs 3 to 5 parallel AI workers running concurrently across orthogonal
 * pedagogical perspectives and rotating subtopic waves. Rapidly produces
 * up to 500+ MCQs without truncation, while strictly preventing question
 * duplication through multi-angle prompting and algorithmic deduplication.
 */
class GeminiMcqService(
    private val apiKey: String = BuildConfig.GEMINI_API_KEY
) {
    companion object {
        private const val TAG = "GeminiMcqService"
        // Priority list of fast Flash models with automatic fallback for rate-limit / overload protection
        private val MODEL_CANDIDATES = listOf(
            "gemini-3.5-flash-lite",
            "gemini-3.6-flash",
            "gemini-3.1-flash-lite"
        )
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }

    private val httpClient = OkHttpClient.Builder()
        .dispatcher(okhttp3.Dispatcher().apply {
            maxRequests = 64
            maxRequestsPerHost = 32
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Distinct pedagogical perspectives assigned to each of the parallel AI workers.
     * Partitioning the subject domain prevents workers from generating repetitive questions.
     */
    private data class AiWorkerPerspective(
        val workerId: Int,
        val title: String,
        val focusGuidance: String
    )

    private val workerPerspectives = listOf(
        AiWorkerPerspective(
            workerId = 1,
            title = "Core Principles & Definitions",
            focusGuidance = "Focus EXCLUSIVELY on foundational principles, primary definitions, laws, terminology, and core concepts. Do NOT include numerical calculation questions."
        ),
        AiWorkerPerspective(
            workerId = 2,
            title = "Mathematical Formulations & Dependencies",
            focusGuidance = "Focus EXCLUSIVELY on equations, formulas, proportional relationships, units of measurement, dimensional analysis, and quantitative relations between variables."
        ),
        AiWorkerPerspective(
            workerId = 3,
            title = "Real-World Applications & Lab Experiments",
            focusGuidance = "Focus EXCLUSIVELY on practical everyday applications, physical experiments, laboratory apparatus, real-world case scenarios, and observed phenomena."
        ),
        AiWorkerPerspective(
            workerId = 4,
            title = "Exceptions, Misconceptions & Tricky Pitfalls",
            focusGuidance = "Focus EXCLUSIVELY on common student misconceptions, subtle distinctions between similar terms, boundary conditions, edge cases, and exceptions to standard rules."
        ),
        AiWorkerPerspective(
            workerId = 5,
            title = "Analytical Reasoning & Cause-and-Effect",
            focusGuidance = "Focus EXCLUSIVELY on multi-step reasoning, assertion-reasoning, cause-and-effect sequences, trends, graphical interpretations, and conceptual comparisons."
        )
    )

    /**
     * Generates MCQs based on structured setup form fields using 3 to 5 parallel AI workers in waves.
     */
    suspend fun generateFromSetup(
        className: String,
        subject: String,
        topic: String,
        scope: String,
        difficulty: String,
        totalCount: Int,
        onProgress: (generatedSoFar: Int, total: Int) -> Unit = { _, _ -> },
        onBatchReceived: (List<GeneratedMcq>) -> Unit = {}
    ): Result<List<GeneratedMcq>> = withContext(Dispatchers.IO) {
        val promptBuilder = StringBuilder()
        promptBuilder.append("Target Audience: $className students.\n")
        promptBuilder.append("Subject: $subject\n")
        promptBuilder.append("Chapter/Topic: $topic\n")
        promptBuilder.append("Scope: $scope\n")
        promptBuilder.append("Difficulty Level: $difficulty\n")

        generateMultiAiParallel(
            baseContext = promptBuilder.toString(),
            subjectHint = subject,
            topicHint = topic,
            difficultyHint = difficulty,
            totalCount = totalCount,
            onProgress = onProgress,
            onBatchReceived = onBatchReceived
        )
    }

    /**
     * Generates MCQs based on user's natural language prompt using 3 to 5 parallel AI workers in waves.
     */
    suspend fun generateFromPrompt(
        userPrompt: String,
        questionType: String,
        language: String,
        includeExplanations: Boolean,
        includeNumericals: Boolean,
        examPattern: String?,
        totalCount: Int,
        onProgress: (generatedSoFar: Int, total: Int) -> Unit = { _, _ -> },
        onBatchReceived: (List<GeneratedMcq>) -> Unit = {}
    ): Result<List<GeneratedMcq>> = withContext(Dispatchers.IO) {
        val promptBuilder = StringBuilder()
        promptBuilder.append("User Prompt: $userPrompt\n")
        promptBuilder.append("Question Type: $questionType\n")
        promptBuilder.append("Language: $language\n")
        promptBuilder.append("Include Explanations: $includeExplanations\n")
        promptBuilder.append("Include Numerical Questions: $includeNumericals\n")
        if (!examPattern.isNullOrBlank()) {
            promptBuilder.append("Exam Pattern: $examPattern\n")
        }

        generateMultiAiParallel(
            baseContext = promptBuilder.toString(),
            subjectHint = "AI Prompt",
            topicHint = userPrompt.take(30),
            difficultyHint = "Medium",
            totalCount = totalCount,
            onProgress = onProgress,
            onBatchReceived = onBatchReceived
        )
    }

    /**
     * Generates MCQs based on scanned image notes / page content descriptions using 3 to 5 parallel AI workers in waves.
     */
    suspend fun generateFromImage(
        pageDescriptions: List<String>,
        difficulty: String,
        totalCount: Int,
        onProgress: (generatedSoFar: Int, total: Int) -> Unit = { _, _ -> },
        onBatchReceived: (List<GeneratedMcq>) -> Unit = {}
    ): Result<List<GeneratedMcq>> = withContext(Dispatchers.IO) {
        val promptBuilder = StringBuilder()
        promptBuilder.append("OCR Extracted Notes Content:\n")
        pageDescriptions.forEachIndexed { index, page ->
            promptBuilder.append("Page ${index + 1}: $page\n")
        }
        promptBuilder.append("Difficulty: $difficulty\n")

        generateMultiAiParallel(
            baseContext = promptBuilder.toString(),
            subjectHint = "OCR Notes",
            topicHint = "Scanned Pages",
            difficultyHint = difficulty,
            totalCount = totalCount,
            onProgress = onProgress,
            onBatchReceived = onBatchReceived
        )
    }

    /**
     * Core Multi-AI Parallel Wave Generation Engine.
     *
     * Dispatches 3 to 5 parallel AI workers simultaneously per wave. Each worker generates
     * an optimal chunk of 5 to 10 questions. For large batches (such as 50, 100, 200, 500 MCQs),
     * parallel waves run consecutively with rotating subtopic angles so the model never
     * truncates, output tokens are strictly protected, and questions never repeat.
     */
    private suspend fun generateMultiAiParallel(
        baseContext: String,
        subjectHint: String,
        topicHint: String,
        difficultyHint: String,
        totalCount: Int,
        onProgress: (generatedSoFar: Int, total: Int) -> Unit,
        onBatchReceived: (List<GeneratedMcq>) -> Unit
    ): Result<List<GeneratedMcq>> = withContext(Dispatchers.IO) {
        val target = if (totalCount <= 0) 10 else totalCount
        val collectedMcqs = mutableListOf<GeneratedMcq>()
        val mutex = Mutex()

        // Rotating thematic sub-facets to guarantee 500 questions never repeat
        val waveThemes = listOf(
            "Core principles, foundational definitions, standard terminology, and primary laws",
            "Governing equations, mathematical relationships, formula derivations, units, and dimensional dependencies",
            "Practical applications, laboratory setups, observation apparatus, and real-world mechanisms",
            "Common student misconceptions, subtle distinctions between confusing terms, and critical boundary limits",
            "Analytical reasoning, graphical interpretation (slopes, areas, curves), and cause-and-effect sequences",
            "Comparative scenarios, ratios, dimensional analysis, and proportional scaling factors",
            "Edge cases, exceptions to standard rules, and special constraint scenarios",
            "Assertion-reasoning questions, multi-step logical deductions, and statement validations",
            "Experimental techniques, error estimation, instrument precision, and physical constants",
            "Advanced multi-concept synthesis and practical engineering/scientific problem solving"
        )

        var waveIndex = 0
        // Ample wave ceiling so even large requests (like 500 MCQs) comfortably complete
        val maxWaves = ((target + 14) / 15).coerceAtLeast(1) + 15

        Log.d(TAG, "Starting Multi-AI Parallel Generation for target: $target questions across waves")

        while (collectedMcqs.size < target && waveIndex < maxWaves) {
            val needed = target - collectedMcqs.size

            // Number of concurrent AI workers for this wave
            val numWorkers = if (target <= 3) 3 else 5

            // Compute optimal batch per worker to generate quickly in fewer waves
            val basePerWorker = when {
                target <= 5 -> ((needed + numWorkers - 1) / numWorkers).coerceIn(1, 2)
                target <= 15 -> ((needed + numWorkers - 1) / numWorkers).coerceIn(2, 4)
                target <= 50 -> ((needed + numWorkers - 1) / numWorkers).coerceIn(3, 10)
                target <= 100 -> ((needed + numWorkers - 1) / numWorkers).coerceIn(5, 12)
                else -> ((needed + numWorkers - 1) / numWorkers).coerceIn(5, 15)
            }
            val currentWaveTheme = waveThemes[waveIndex % waveThemes.size]

            // Avoid repeating question titles already generated
            val avoidStems = mutex.withLock {
                collectedMcqs.takeLast(10).map { it.question }
            }

            val activeWorkers = workerPerspectives.take(numWorkers)

            // Execute all AI workers concurrently in parallel!
            coroutineScope {
                val workerJobs = activeWorkers.map { worker ->
                    async(Dispatchers.IO) {
                        val workerPrompt = buildWorkerPrompt(
                            baseContext = baseContext,
                            worker = worker,
                            totalWorkers = numWorkers,
                            requestedCount = basePerWorker,
                            waveTheme = currentWaveTheme,
                            avoidStems = avoidStems
                        )

                        val result = callModelWithRetry(
                            prompt = workerPrompt,
                            subjectHint = subjectHint,
                            topicHint = topicHint,
                            difficultyHint = difficultyHint
                        )

                        if (result.isSuccess) {
                            val questions = result.getOrNull().orEmpty()
                            mutex.withLock {
                                val combined = collectedMcqs + questions
                                val unique = deduplicateQuestions(combined)
                                collectedMcqs.clear()
                                collectedMcqs.addAll(unique)

                                val currentAccepted = minOf(collectedMcqs.size, target)
                                onProgress(currentAccepted, target)
                                onBatchReceived(collectedMcqs.take(target))
                            }
                        } else {
                            Log.w(TAG, "Worker ${worker.workerId} wave $waveIndex failed: ${result.exceptionOrNull()?.message}")
                        }
                    }
                }
                workerJobs.awaitAll()
            }

            waveIndex++

            if (collectedMcqs.size >= target) {
                break
            }

            // Brief pause between waves to prevent server rate limiting
            delay(100L)
        }

        if (collectedMcqs.isEmpty()) {
            return@withContext Result.failure(
                Exception("Could not generate MCQs at this time. Please check your network or try again.")
            )
        }

        val finalResult = collectedMcqs.take(target)
        onProgress(finalResult.size, target)
        onBatchReceived(finalResult)
        Result.success(finalResult)
    }

    /**
     * Constructs worker-specific prompt with strict non-overlapping instructions and wave subtopic focus.
     */
    private fun buildWorkerPrompt(
        baseContext: String,
        worker: AiWorkerPerspective,
        totalWorkers: Int,
        requestedCount: Int,
        waveTheme: String,
        avoidStems: List<String>
    ): String {
        val avoidInstruction = if (avoidStems.isNotEmpty()) {
            val stemsFormatted = avoidStems.take(10).joinToString("\n- ")
            "\nDO NOT repeat or duplicate any questions similar to these:\n- $stemsFormatted\n"
        } else ""

        return """
            $baseContext
            
            PARALLEL AI WORKER #${worker.workerId} of $totalWorkers (${worker.title}):
            ${worker.focusGuidance}
            
            WAVE TOPIC FOCUS:
            $waveTheme
            $avoidInstruction
            STRICT DEDUPLICATION RULE:
            Make sure your questions are completely unique, highly specific to your focus area, and not repeated.
            Do NOT repeat the same concept across questions.
            
            Generate exactly $requestedCount high quality multiple choice questions.
            Each question must have exactly 4 plausible options.
            One option must be strictly correct.
            Keep each explanation strictly to 1 concise sentence for ultra-fast generation.
            
            You MUST return ONLY a valid JSON array of objects. Do NOT include markdown code fences (no ```json or ```).
            Do NOT include any text outside the JSON array.
            
            JSON schema:
            [
              {
                "question": "string",
                "options": ["string", "string", "string", "string"],
                "correctAnswerIndex": 0,
                "explanation": "string",
                "difficulty": "string",
                "subject": "string",
                "topic": "string"
              }
            ]
        """.trimIndent()
    }

    /**
     * Precision deduplication filter:
     * 1. Strips leading question numbers and symbols.
     * 2. Drops exact matches.
     * 3. Drops near-identical phrasing (>= 85% word overlap).
     * 4. Drops high word overlap (>= 70%) only if they share the exact same correct answer.
     * (Prevents dropping valid distinct questions that happen to share standard unit/formula options).
     */
    private fun deduplicateQuestions(rawQuestions: List<GeneratedMcq>): List<GeneratedMcq> {
        val unique = mutableListOf<GeneratedMcq>()
        for (mcq in rawQuestions) {
            val normalized = normalizeQuestion(mcq.question)
            if (normalized.length < 5) continue

            if (unique.none { isSemanticDuplicate(mcq, it) }) {
                unique.add(mcq)
            } else {
                Log.d(TAG, "Deduplication: Filtered duplicate: ${mcq.question}")
            }
        }
        return unique
    }

    private fun isSemanticDuplicate(mcq: GeneratedMcq, existing: GeneratedMcq): Boolean {
        val norm1 = normalizeQuestion(mcq.question)
        val norm2 = normalizeQuestion(existing.question)

        // 1. Exact normalized text match
        if (norm1 == norm2) return true

        val similarity = calculateJaccardSimilarity(norm1, norm2)

        // 2. Extremely high text similarity (near-identical question phrasing)
        if (similarity >= 0.85) return true

        // 3. High text similarity (>= 0.70) AND same correct answer
        val ans1 = mcq.options.getOrNull(mcq.correctAnswerIndex)?.trim()?.lowercase()
        val ans2 = existing.options.getOrNull(existing.correctAnswerIndex)?.trim()?.lowercase()
        if (similarity >= 0.70 && ans1 != null && ans1 == ans2) {
            return true
        }

        return false
    }

    private fun normalizeQuestion(text: String): String {
        return text.lowercase()
            .replace(Regex("^(q\\d+[:.]?|question\\s*\\d+[:.]?|\\d+[.)])\\s*"), "")
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun calculateJaccardSimilarity(s1: String, s2: String): Double {
        val words1 = s1.split(" ").filter { it.length > 2 }.toSet()
        val words2 = s2.split(" ").filter { it.length > 2 }.toSet()
        if (words1.isEmpty() || words2.isEmpty()) return 0.0
        val intersection = words1.intersect(words2).size
        val union = words1.union(words2).size
        return if (union == 0) 0.0 else intersection.toDouble() / union.toDouble()
    }

    private fun areOptionsEquivalent(opt1: List<String>, opt2: List<String>): Boolean {
        val clean1 = opt1.map { it.replace(Regex("^[A-D][.):]\\s*"), "").trim().lowercase() }.toSet()
        val clean2 = opt2.map { it.replace(Regex("^[A-D][.):]\\s*"), "").trim().lowercase() }.toSet()
        return clean1.intersect(clean2).size >= 3
    }

    /**
     * Calls Gemini model and parses JSON response with resilience.
     * Retries once with strict instruction if initial JSON parsing fails.
     */
    private suspend fun callModelWithRetry(
        prompt: String,
        subjectHint: String,
        topicHint: String,
        difficultyHint: String
    ): Result<List<GeneratedMcq>> {
        // Attempt 1: Standard structured prompt
        val firstAttempt = executeRequestWithFallback(prompt)
        if (firstAttempt.isSuccess) {
            val rawText = firstAttempt.getOrNull().orEmpty()
            val parsed = parseMcqJson(rawText, subjectHint, topicHint, difficultyHint)
            if (!parsed.isNullOrEmpty()) {
                return Result.success(parsed)
            }
            Log.w(TAG, "Attempt 1 failed to parse JSON. Retrying with strict JSON instruction...")
        }

        // Attempt 2: Strict retry instruction
        val strictPrompt = """
            $prompt
            
            CRITICAL RETRY INSTRUCTION:
            Your previous response could not be parsed as valid JSON.
            Return valid JSON only, no other text.
            Do NOT wrap in markdown quotes. Start your response directly with '[' and end with ']'.
        """.trimIndent()

        val secondAttempt = executeRequestWithFallback(strictPrompt)
        if (secondAttempt.isSuccess) {
            val rawText = secondAttempt.getOrNull().orEmpty()
            val parsed = parseMcqJson(rawText, subjectHint, topicHint, difficultyHint)
            if (!parsed.isNullOrEmpty()) {
                return Result.success(parsed)
            }
        }

        return Result.failure(
            Exception(secondAttempt.exceptionOrNull()?.message ?: "Failed to parse valid MCQ JSON from AI response.")
        )
    }

    /**
     * Executes HTTP POST to Gemini generateContent endpoint with automatic model fallback
     * to protect against 429 (quota exceeded) and 503 (model overloaded) errors.
     */
    private suspend fun executeRequestWithFallback(
        prompt: String,
        jsonMode: Boolean = true
    ): Result<String> {
        var lastException: Exception? = null

        val effectiveKey = when {
            apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY" && apiKey != "DEFAULT_API_KEY" -> apiKey
            !System.getenv("GEMINI_API_KEY").isNullOrBlank() -> System.getenv("GEMINI_API_KEY") ?: ""
            else -> apiKey
        }

        for (model in MODEL_CANDIDATES) {
            try {
                val url = "$BASE_URL/$model:generateContent?key=$effectiveKey"

                val jsonBody = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            })
                        })
                    }
                    put("contents", contentsArray)

                    put("generationConfig", JSONObject().apply {
                        if (jsonMode) {
                            put("response_mime_type", "application/json")
                        }
                        put("temperature", 0.4)
                        put("max_output_tokens", 8192)
                    })
                }

                val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    val errorMsg = try {
                        val errJson = JSONObject(responseBody)
                        errJson.optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}"
                    } catch (e: Exception) {
                        "HTTP ${response.code}: $responseBody"
                    }
                    Log.w(TAG, "Model $model returned HTTP ${response.code}: $errorMsg")

                    val isQuotaOrOverload = response.code == 429 || response.code == 503 ||
                            responseBody.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ||
                            responseBody.contains("overloaded", ignoreCase = true)

                    if (isQuotaOrOverload) {
                        delay(200L)
                    }
                    lastException = Exception(errorMsg)
                    continue
                }

                val rootJson = JSONObject(responseBody)
                val candidates = rootJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")

                if (!text.isNullOrBlank()) {
                    return Result.success(text)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Request to $model failed: ${e.javaClass.simpleName} - ${e.message}")
                lastException = e
            }
        }

        return Result.failure(lastException ?: Exception("All AI model candidates failed."))
    }

    /**
     * Robustly parses raw model text into a List<GeneratedMcq>.
     * Strips any markdown fences or extraneous text if present.
     * Resiliently salvages completed JSON objects even if truncated mid-stream.
     */
    private fun parseMcqJson(
        rawText: String,
        subjectHint: String,
        topicHint: String,
        difficultyHint: String
    ): List<GeneratedMcq>? {
        try {
            var cleanText = rawText.trim()

            // Remove markdown code fences if present
            if (cleanText.startsWith("```json")) {
                cleanText = cleanText.removePrefix("```json")
            } else if (cleanText.startsWith("```")) {
                cleanText = cleanText.removePrefix("```")
            }
            if (cleanText.endsWith("```")) {
                cleanText = cleanText.removeSuffix("```")
            }
            cleanText = cleanText.trim()

            // Robust JSON array boundary extraction
            val startIdx = cleanText.indexOf('[')
            val endIdx = cleanText.lastIndexOf(']')

            if (startIdx != -1) {
                if (endIdx != -1 && endIdx > startIdx) {
                    cleanText = cleanText.substring(startIdx, endIdx + 1)
                } else {
                    // Truncated array: find the last complete object closing '}'
                    val lastObjectEnd = cleanText.lastIndexOf('}')
                    if (lastObjectEnd != -1 && lastObjectEnd > startIdx) {
                        cleanText = cleanText.substring(startIdx, lastObjectEnd + 1) + "]"
                    }
                }
            }

            val jsonArray = JSONArray(cleanText)
            val list = mutableListOf<GeneratedMcq>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.optJSONObject(i) ?: continue

                val question = obj.optString("question", "").trim()
                if (question.isBlank()) continue

                val optionsArray = obj.optJSONArray("options")
                val options = mutableListOf<String>()
                if (optionsArray != null) {
                    for (j in 0 until optionsArray.length()) {
                        val opt = optionsArray.optString(j, "").trim()
                        if (opt.isNotBlank()) options.add(opt)
                    }
                }

                if (options.size < 2) continue

                // Pad options up to 4 if needed
                while (options.size < 4) {
                    options.add("Option ${('A' + options.size)}")
                }

                var correctIdx = obj.optInt("correctAnswerIndex", 0)
                if (correctIdx < 0 || correctIdx >= options.size) {
                    correctIdx = 0
                }

                val explanation = obj.optString("explanation", "Correct answer is option ${('A' + correctIdx)}.")
                val difficulty = obj.optString("difficulty", difficultyHint).ifBlank { difficultyHint }
                val subject = obj.optString("subject", subjectHint).ifBlank { subjectHint }
                val topic = obj.optString("topic", topicHint).ifBlank { topicHint }

                list.add(
                    GeneratedMcq(
                        question = question,
                        options = options,
                        correctAnswerIndex = correctIdx,
                        explanation = explanation,
                        difficulty = difficulty,
                        subject = subject,
                        topic = topic
                    )
                )
            }

            return list
        } catch (e: Exception) {
            Log.e(TAG, "parseMcqJson error for: $rawText", e)
            return null
        }
    }

    /**
     * Explains a specific MCQ question step-by-step for the user.
     */
    suspend fun askAiExplanation(questionContext: String): String = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                You are a friendly, encouraging expert AI Teacher.
                The student is studying the following multiple-choice question:
                
                $questionContext
                
                Explain step-by-step why the correct option is right, and briefly why common distractors are incorrect.
                Keep your explanation concise, crystal clear, educational, and easy to understand.
                Format clearly with bullet points or short paragraphs.
            """.trimIndent()

            val result = executeRequestWithFallback(prompt, jsonMode = false)
            if (result.isSuccess) {
                result.getOrNull()?.trim() ?: "No explanation available."
            } else {
                "Unable to load AI explanation at this moment. (${result.exceptionOrNull()?.message})"
            }
        } catch (e: Exception) {
            Log.e(TAG, "askAiExplanation error", e)
            "Could not connect to AI Teacher: ${e.message}"
        }
    }
}
