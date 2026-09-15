package com.example.data.model

/**
 * Curriculum-accurate lookup table: SyllabusData.syllabus[Class][Subject] = [chapters]
 *
 * Sources:
 *  - Class 6, 7, 8, and Class 9-10 English: current NCERT 2024-25 books: Poorvi, Malhar,
 *    Ganita Prakash, Curiosity, Exploring Society India and Beyond, Kshitij, Kritika,
 *    First Flight, Footprints without Feet.
 *  - Class 9-10 Mathematics / Science / Social Science, Class 1-5 and Class 11-12: standard NCERT/CBSE topic lists.
 *  - NEET: NTA-notified NEET UG syllabus (Physics, Chemistry, Biology, Class 11 + 12 combined).
 *  - JEE: JEE Main/Advanced syllabus (Physics, Chemistry, Mathematics, Class 11 + 12 combined).
 *  - Dropper / Target: repeater syllabus for NEET & JEE.
 */
object SyllabusData {

    val syllabus: Map<String, Map<String, List<String>>> = mapOf(
        // ---------------------------------------------------------------------
        // CLASS 6 (NCERT 2024-25: Poorvi / Ganita Prakash / Curiosity / Exploring Society)
        // ---------------------------------------------------------------------
        "Class 6" to mapOf(
            "English" to listOf(
                "Fables and Folk Tales", "Friendship", "Nurturing Nature",
                "Sports and Wellness", "Culture and Tradition"
            ),
            "Mathematics" to listOf(
                "Patterns in Mathematics", "Lines and Angles", "Number Play",
                "Data Handling and Presentation", "Prime Time", "Perimeter and Area",
                "Fractions", "Playing with Constructions", "Symmetry", "The Other Side of Zero"
            ),
            "Science" to listOf(
                "The Wonderful World of Science", "Diversity in the Living World",
                "Mindful Eating: A Path to a Healthy Body", "Exploring Magnets",
                "Measurement of Length and Motion", "Materials Around Us",
                "Temperature and its Measurement", "A Journey through States of Water",
                "Methods of Separation in Everyday Life", "Living Creatures: Exploring their Characteristics",
                "Nature's Treasures", "Beyond Earth"
            ),
            "Social Science" to listOf(
                "Locating Places on the Earth", "Oceans and Continents", "Landforms and Life",
                "Timeline and Sources of History", "India, That Is Bharat",
                "The Beginnings of Indian Civilisation", "India's Cultural Roots",
                "Unity in Diversity, or Many in the One", "Family and Community",
                "Grassroots Democracy – Part 1: Governance",
                "Grassroots Democracy – Part 2: Local Government in Rural Areas",
                "Grassroots Democracy – Part 3: Local Government in Urban Areas",
                "The Value of Work", "Economic Activities Around Us"
            )
        ),

        // ---------------------------------------------------------------------
        // CLASS 7
        // ---------------------------------------------------------------------
        "Class 7" to mapOf(
            "English" to listOf(
                "The Day the River Spoke / Try Again / Three Days to See",
                "Animals, Birds, and Dr. Dolittle / A Funny Man / Say the Right Thing",
                "My Brother's Great Invention / Paper Boats / North, South, East, West",
                "The Tunnel / Travel / Conquering the Summit",
                "A Homage to Our Brave Soldiers / My Dear Soldiers / Rani Abbakka"
            ),
            "Mathematics" to listOf(
                "Large Numbers Around Us", "Arithmetic Expressions", "A Peek Beyond the Point",
                "Expressions using Letter-Numbers", "Parallel and Intersecting Lines",
                "Number Play", "A Tale of Three Intersecting Lines", "Working with Fractions"
            ),
            "Science" to listOf(
                "The Ever-Evolving World of Science", "Exploring Substances: Acidic, Basic, and Neutral",
                "Electricity: Circuits and their Components", "The World of Metals and Non-metals",
                "Changes Around Us: Physical and Chemical", "Adolescence: A Stage of Growth and Change",
                "Heat Transfer in Nature", "Measurement of Time and Motion",
                "Life Processes in Animals", "Life Processes in Plants", "Light: Shadows and Reflection",
                "Earth, Moon, and the Sun"
            ),
            "Social Science" to listOf(
                "Geographical Diversity of India", "Understanding the Weather", "Climates of India",
                "New Beginnings: Cities and States", "The Rise of Empires", "The Age of Reorganisation",
                "The Gupta Era: An Age of Tireless Creativity", "How the Land Becomes Sacred",
                "From the Rulers to the Ruled: Types of Governments",
                "The Constitution of India – An Introduction", "From Barter to Money", "Understanding Markets"
            )
        ),

        // ---------------------------------------------------------------------
        // CLASS 8
        // ---------------------------------------------------------------------
        "Class 8" to mapOf(
            "English" to listOf(
                "The Wit that Won Hearts / A Concrete Example / Wisdom Paves the Way",
                "A Tale of Valour / Somebody's Mother / Verghese Kurien",
                "The Case of the Fifth Word / The Magic Brush of Dreams / Spectacular Wonders",
                "The Cherry Tree / Harvest Hymn / Waiting for the Rain",
                "Feathered Friend / Magnifying Glass / Bibha Chowdhuri"
            ),
            "Mathematics" to listOf(
                "A Square and A Cube", "Power Play", "A Story of Numbers", "Quadrilaterals",
                "Number Play", "We Distribute, Yet Things Multiply", "Proportional Reasoning"
            ),
            "Science" to listOf(
                "Exploring the Investigative World of Science", "The Invisible Living World: Beyond Our Naked Eye",
                "Health: The Ultimate Treasure", "Electricity: Magnetic and Heating Effects", "Exploring Forces",
                "Pressure, Winds, Storms, and Cyclones", "Particulate Nature of Matter",
                "Nature of Matter: Elements, Compounds, and Mixtures",
                "The Amazing World of Solutes, Solvents, and Solutions", "Light: Mirrors and Lenses",
                "Keeping Time with the Skies", "How Nature Works in Harmony",
                "Our Home: Earth, a Unique Life Sustaining Planet"
            ),
            "Social Science" to listOf(
                "Natural Resources and Their Use", "Reshaping India's Political Map",
                "The Rise of the Marathas", "The Colonial Era in India",
                "Universal Franchise and India's Electoral System",
                "The Parliamentary System: Legislature and Executive", "Factors of Production"
            )
        ),

        // ---------------------------------------------------------------------
        // CLASS 9
        // ---------------------------------------------------------------------
        "Class 9" to mapOf(
            "English" to listOf(
                "How I Taught My Grandmother to Read; Bharat Our Land",
                "The Pot Maker; Gifts of Grace: Honouring Our Vocations",
                "Winds of Change; Canvas of Soil",
                "Vitamin-M; I Cannot Remember My Mother",
                "The World of Limitless Possibilities; Nine Gold Medals",
                "Twin Melodies; A Friend Found in Music",
                "Carrier of Words; Words",
                "Follow That Dream; Believe in Yourself"
            ),
            "Mathematics" to listOf(
                "Number Systems", "Polynomials", "Coordinate Geometry", "Linear Equations in Two Variables",
                "Introduction to Euclid's Geometry", "Lines and Angles", "Triangles", "Quadrilaterals",
                "Circles", "Heron's Formula", "Surface Areas and Volumes", "Statistics", "Probability"
            ),
            "Science" to listOf(
                "Exploration: Entering the World of Secondary Science", "Cell: The Building Block of Life",
                "Tissues in Action", "Describing Motion Around Us", "Exploring Mixtures and their Separation",
                "How Forces Affect Motion", "Work, Energy, and Simple Machines", "Journey Inside the Atom",
                "Atomic Foundations of Matter", "Sound Waves: Characteristics and Applications",
                "Reproduction: How Life Continues", "Patterns in Life: Diversity and Classification",
                "Earth as a System: Energy, Matter, and Life"
            ),
            "Social Science" to listOf(
                "The French Revolution", "Socialism in Europe and the Russian Revolution",
                "Nazism and the Rise of Hitler", "Forest Society and Colonialism",
                "Pastoralists in the Modern World", "India – Size and Location",
                "Physical Features of India", "Drainage", "Climate", "Natural Vegetation and Wildlife",
                "Population", "What is Democracy? Why Democracy?", "Constitutional Design",
                "Electoral Politics", "Working of Institutions", "Democratic Rights",
                "The Story of Village Palampur", "People as Resource", "Poverty as a Challenge",
                "Food Security in India"
            )
        ),

        // ---------------------------------------------------------------------
        // CLASS 10
        // ---------------------------------------------------------------------
        "Class 10" to mapOf(
            "English" to listOf(
                "A Letter to God", "Nelson Mandela: Long Walk to Freedom", "Two Stories about Flying",
                "From the Diary of Anne Frank", "Glimpses of India", "Mijbil the Otter",
                "Madam Rides the Bus", "The Sermon at Benares", "The Proposal",
                "A Triumph of Surgery", "The Thief's Story", "The Midnight Visitor",
                "A Question of Trust", "Footprints without Feet", "The Making of a Scientist",
                "The Necklace", "Bholi", "The Book That Saved the Earth"
            ),
            "Mathematics" to listOf(
                "Real Numbers", "Polynomials", "Pair of Linear Equations in Two Variables",
                "Quadratic Equations", "Arithmetic Progressions", "Triangles", "Coordinate Geometry",
                "Introduction to Trigonometry", "Applications of Trigonometry", "Circles",
                "Areas Related to Circles", "Surface Areas and Volumes", "Statistics", "Probability"
            ),
            "Science" to listOf(
                "Chemical Reactions and Equations", "Acids, Bases and Salts", "Metals and Non-metals",
                "Carbon and its Compounds", "Life Processes", "Control and Coordination",
                "How do Organisms Reproduce?", "Heredity and Evolution", "Light – Reflection and Refraction",
                "The Human Eye and the Colourful World", "Electricity", "Magnetic Effects of Electric Current",
                "Our Environment", "Management of Natural Resources"
            ),
            "Social Science" to listOf(
                "The Rise of Nationalism in Europe", "Nationalism in India",
                "The Making of a Global World", "The Age of Industrialisation", "Print Culture and the Modern World",
                "Resources and Development", "Forest and Wildlife Resources", "Water Resources",
                "Agriculture", "Minerals and Energy Resources", "Manufacturing Industries", "Lifelines of National Economy",
                "Power-sharing", "Federalism", "Gender, Religion and Caste", "Political Parties", "Outcomes of Democracy",
                "Development", "Sectors of the Indian Economy", "Money and Credit", "Globalisation and the Indian Economy",
                "Consumer Rights"
            )
        ),

        // ---------------------------------------------------------------------
        // CLASS 11 (standard NCERT/CBSE)
        // ---------------------------------------------------------------------
        "Class 11" to mapOf(
            "Physics" to listOf(
                "Physical World and Measurement", "Kinematics", "Laws of Motion",
                "Work, Energy and Power", "System of Particles and Rotational Motion",
                "Gravitation", "Mechanical Properties of Solids", "Mechanical Properties of Fluids",
                "Thermal Properties of Matter", "Thermodynamics", "Kinetic Theory",
                "Oscillations", "Waves"
            ),
            "Chemistry" to listOf(
                "Some Basic Concepts of Chemistry", "Structure of Atom", "Classification of Elements and Periodicity in Properties",
                "Chemical Bonding and Molecular Structure", "States of Matter", "Thermodynamics",
                "Equilibrium", "Redox Reactions", "Hydrogen", "The s-Block Elements",
                "The p-Block Elements", "Organic Chemistry – Some Basic Principles and Techniques",
                "Hydrocarbons", "Environmental Chemistry"
            ),
            "Biology" to listOf(
                "The Living World", "Biological Classification", "Plant Kingdom", "Animal Kingdom",
                "Morphology of Flowering Plants", "Anatomy of Flowering Plants", "Structural Organisation in Animals",
                "Cell: The Unit of Life", "Biomolecules", "Cell Cycle and Cell Division",
                "Transport in Plants", "Mineral Nutrition", "Photosynthesis in Higher Plants",
                "Respiration in Plants", "Plant Growth and Development", "Digestion and Absorption",
                "Breathing and Exchange of Gases", "Body Fluids and Circulation", "Excretory Products and their Elimination",
                "Locomotion and Movement", "Neural Control and Coordination", "Chemical Coordination and Integration"
            ),
            "Mathematics" to listOf(
                "Sets", "Relations and Functions", "Trigonometric Functions",
                "Principle of Mathematical Induction", "Complex Numbers and Quadratic Equations",
                "Linear Inequalities", "Permutations and Combinations", "Binomial Theorem",
                "Sequences and Series", "Straight Lines", "Conic Sections",
                "Introduction to Three Dimensional Geometry", "Limits and Derivatives",
                "Mathematical Reasoning", "Statistics", "Probability"
            ),
            "English" to listOf(
                "The Portrait of a Lady", "We're Not Afraid to Die... if We Can All Be Together",
                "Discovering Tut: the Saga Continues", "Landscape of the Soul",
                "The Ailing Planet: the Green Movement's Role", "The Browning Version",
                "The Adventure", "Silk Road", "Father to Son", "A Photograph",
                "The Laburnum Top", "The Voice of the Rain", "Childhood", "The Summer of the Beautiful White Horse"
            ),
            "Computer Science" to listOf(
                "Computer System Basics", "Encoding Schemes and Number Systems", "Emerging Trends",
                "Introduction to Problem Solving", "Getting Started with Python",
                "Flow of Control", "Functions", "Strings", "Lists", "Tuples and Dictionaries",
                "Societal Impacts of Digital Footprint", "Data Handling using Pandas",
                "Database Concepts", "Structured Query Language"
            ),
            "Social Science" to listOf(
                "From the Beginning of Time (History)", "Writing and City Life", "An Empire Across Three Continents",
                "The Central Islamic Lands", "Nomadic Empires", "The Three Orders",
                "India – Location", "Structure and Physiography", "Drainage System",
                "Climate", "Natural Vegetation", "Constitution: Why and How?",
                "Rights in the Indian Constitution", "Election and Representation",
                "Executive", "Legislature", "Judiciary", "Human Geography: Nature and Scope",
                "Indian Economy on the Eve of Independence"
            )
        ),

        // ---------------------------------------------------------------------
        // CLASS 12 (standard NCERT/CBSE)
        // ---------------------------------------------------------------------
        "Class 12" to mapOf(
            "Physics" to listOf(
                "Electric Charges and Fields", "Electrostatic Potential and Capacitance", "Current Electricity",
                "Moving Charges and Magnetism", "Magnetism and Matter", "Electromagnetic Induction",
                "Alternating Current", "Electromagnetic Waves", "Ray Optics and Optical Instruments",
                "Wave Optics", "Dual Nature of Radiation and Matter", "Atoms", "Nuclei",
                "Semiconductor Electronics"
            ),
            "Chemistry" to listOf(
                "Solutions", "Electrochemistry", "Chemical Kinetics", "d and f Block Elements",
                "Coordination Compounds", "Haloalkanes and Haloarenes", "Alcohols, Phenols and Ethers",
                "Aldehydes, Ketones and Carboxylic Acids", "Amines", "Biomolecules",
                "Polymers", "Chemistry in Everyday Life"
            ),
            "Biology" to listOf(
                "Sexual Reproduction in Flowering Plants", "Human Reproduction", "Reproductive Health",
                "Principles of Inheritance and Variation", "Molecular Basis of Inheritance", "Evolution",
                "Human Health and Disease", "Microbes in Human Welfare",
                "Biotechnology: Principles and Processes", "Biotechnology and its Applications",
                "Organisms and Populations", "Ecosystem", "Biodiversity and Conservation"
            ),
            "Mathematics" to listOf(
                "Relations and Functions", "Inverse Trigonometric Functions", "Matrices", "Determinants",
                "Continuity and Differentiability", "Application of Derivatives", "Integrals",
                "Application of Integrals", "Differential Equations", "Vector Algebra",
                "Three Dimensional Geometry", "Linear Programming", "Probability"
            ),
            "English" to listOf(
                "The Last Lesson", "Lost Spring", "Deep Water", "The Rattrap", "Indigo",
                "Poets and Pancakes", "The Interview", "Going Places",
                "My Mother at Sixty-six", "An Elementary School Classroom in a Slum",
                "Keeping Quiet", "A Thing of Beauty", "Aunt Jennifer's Tigers",
                "The Third Level", "The Tiger King", "Journey to the End of the Earth",
                "The Enemy", "Should Wizard Hit Mommy", "On the Face of It", "Evans Tries an O-Level"
            ),
            "Computer Science" to listOf(
                "Revision of Python", "Functions", "File Handling", "Stacks",
                "Queues and Deques", "Sorting", "Understanding Data", "Database Concepts",
                "Structured Query Language", "Computer Networks", "Data Communication",
                "Security Aspects"
            ),
            "Social Science" to listOf(
                "Bricks, Beads and Bones: The Harappan Civilisation", "Kings, Farmers and Towns",
                "Kinship, Caste and Class", "Thinkers, Beliefs and Buildings", "Through the Eyes of Travellers",
                "Bhakti–Sufi Traditions", "An Imperial Capital: Vijayanagara", "Peasants, Zamindars and the State",
                "Kings and Chronicles: The Mughal Courts", "Colonialism and the Countryside",
                "Rebels and the Raj: 1857 Revolt", "Colonial Cities", "Mahatma Gandhi and the Nationalist Movement",
                "Understanding Partition", "Framing the Constitution", "The Cold War Era",
                "The End of Bipolarity", "US Hegemony in World Politics", "Alternative Centres of Power",
                "Contemporary South Asia", "International Organisations", "Security in the Contemporary World",
                "Environment and Natural Resources", "Globalisation", "Challenges of Nation Building",
                "Era of One-Party Dominance", "Politics of Planned Development", "India's External Relations",
                "The Crisis of Democratic Order", "Rise of Popular Movements", "Regional Aspirations",
                "Recent Developments in Indian Politics", "Human Geography: Nature and Scope",
                "The World Population", "Population Composition", "Human Development",
                "Primary Activities", "Secondary Activities", "Tertiary and Quaternary Activities",
                "Transport and Communication", "International Trade", "Human Settlements",
                "Introduction to Microeconomics", "Indian Economic Development"
            )
        ),

        // ---------------------------------------------------------------------
        // NEET (NTA-notified syllabus, Class 11 + 12 combined)
        // ---------------------------------------------------------------------
        "NEET" to mapOf(
            "Physics" to listOf(
                "Physical World and Measurement", "Kinematics", "Laws of Motion", "Work, Energy and Power",
                "Rotational Motion", "Gravitation", "Properties of Bulk Matter", "Thermodynamics",
                "Kinetic Theory of Gases", "Oscillations and Waves", "Electrostatics",
                "Current Electricity", "Magnetic Effects of Current and Magnetism",
                "Electromagnetic Induction and Alternating Current", "Electromagnetic Waves",
                "Optics", "Dual Nature of Matter and Radiation", "Atoms and Nuclei",
                "Electronic Devices", "Experimental Skills"
            ),
            "Chemistry" to listOf(
                "Some Basic Concepts of Chemistry", "Structure of Atom", "Classification of Elements and Periodicity",
                "Chemical Bonding and Molecular Structure", "States of Matter", "Thermodynamics",
                "Equilibrium", "Redox Reactions", "Hydrogen", "s-Block Elements", "p-Block Elements",
                "Organic Chemistry – Basic Principles", "Hydrocarbons", "Environmental Chemistry",
                "Solid State", "Solutions", "Electrochemistry", "Chemical Kinetics", "Surface Chemistry",
                "General Principles of Isolation of Elements", "p-Block Elements (Class 12)",
                "d and f Block Elements", "Coordination Compounds", "Haloalkanes and Haloarenes",
                "Alcohols, Phenols and Ethers", "Aldehydes, Ketones and Carboxylic Acids",
                "Organic Compounds Containing Nitrogen", "Biomolecules", "Polymers", "Chemistry in Everyday Life"
            ),
            "Biology" to listOf(
                "Diversity in Living World", "Structural Organisation in Animals and Plants",
                "Cell Structure and Function", "Plant Physiology", "Human Physiology",
                "Reproduction", "Genetics and Evolution", "Biology and Human Welfare",
                "Biotechnology and its Applications", "Ecology and Environment"
            )
        ),

        // ---------------------------------------------------------------------
        // JEE (Main + Advanced, Class 11 + 12 combined)
        // ---------------------------------------------------------------------
        "JEE" to mapOf(
            "Physics" to listOf(
                "Units and Measurements", "Kinematics", "Laws of Motion", "Work, Energy and Power",
                "Rotational Motion", "Gravitation", "Properties of Solids and Liquids", "Thermodynamics",
                "Kinetic Theory of Gases", "Oscillations and Waves", "Electrostatics", "Current Electricity",
                "Magnetic Effects of Current and Magnetism", "Electromagnetic Induction and Alternating Currents",
                "Electromagnetic Waves", "Optics", "Dual Nature of Matter and Radiation", "Atoms and Nuclei",
                "Electronic Devices", "Experimental Skills"
            ),
            "Chemistry" to listOf(
                "Some Basic Concepts in Chemistry", "Atomic Structure", "Chemical Bonding and Molecular Structure",
                "Chemical Thermodynamics", "Solutions", "Equilibrium", "Redox Reactions and Electrochemistry",
                "Chemical Kinetics", "Classification of Elements and Periodicity", "p-Block Elements",
                "d and f Block Elements", "Coordination Compounds", "Purification and Characterisation of Organic Compounds",
                "Some Basic Principles of Organic Chemistry", "Hydrocarbons", "Organic Compounds Containing Halogens",
                "Organic Compounds Containing Oxygen", "Organic Compounds Containing Nitrogen", "Biomolecules",
                "Principles Related to Practical Chemistry"
            ),
            "Mathematics" to listOf(
                "Sets, Relations and Functions", "Complex Numbers and Quadratic Equations", "Matrices and Determinants",
                "Permutations and Combinations", "Binomial Theorem", "Sequences and Series", "Limit, Continuity and Differentiability",
                "Integral Calculus", "Differential Equations", "Coordinate Geometry", "Three Dimensional Geometry",
                "Vector Algebra", "Statistics and Probability", "Trigonometry", "Mathematical Reasoning"
            )
        ),

        // ---------------------------------------------------------------------
        // Dropper / Target (repeaters — reuses full NEET + JEE bank)
        // ---------------------------------------------------------------------
        "Dropper / Target" to mapOf(
            "Physics" to listOf(
                "Mechanics (Class 11 + 12 combined)", "Thermodynamics and Kinetic Theory",
                "Oscillations and Waves", "Electrostatics and Current Electricity",
                "Magnetism and Electromagnetic Induction", "Optics", "Modern Physics",
                "Electronic Devices"
            ),
            "Chemistry" to listOf(
                "Physical Chemistry (Full Syllabus)", "Inorganic Chemistry (Full Syllabus)",
                "Organic Chemistry (Full Syllabus)"
            ),
            "Biology" to listOf(
                "Diversity in Living World", "Cell Structure and Function", "Plant Physiology",
                "Human Physiology", "Reproduction", "Genetics and Evolution",
                "Biology and Human Welfare", "Biotechnology", "Ecology and Environment"
            ),
            "Mathematics" to listOf(
                "Algebra", "Trigonometry", "Coordinate Geometry", "Calculus",
                "Vectors and 3D Geometry", "Statistics and Probability"
            )
        )
    )

    /**
     * All supported class/category labels in curriculum order.
     */
    fun getClasses(): List<String> {
        return syllabus.keys.toList()
    }

    /**
     * Returns the list of subjects available for a given class.
     * Prevents invalid combinations (e.g., Class 1-5 never offers Physics, Class 11-12 never offers EVS).
     */
    fun getSubjects(className: String): List<String> {
        return syllabus[className]?.keys?.toList().orEmpty()
    }

    /**
     * Returns the chapter list for a given class + subject.
     * Falls back to an empty list (never throws) if the combination doesn't exist.
     */
    fun getChapters(className: String, subjectName: String): List<String> {
        return syllabus[className]?.get(subjectName).orEmpty()
    }

    /**
     * Generation payload builder matching the user specification.
     */
    data class GenerationPayload(
        val className: String,
        val subject: String,
        val chapter: String,
        val scope: String, // "entireChapter" or "specificTopic"
        val specificTopic: String? = null
    )

    fun buildGenerationPayload(
        selectedClass: String,
        selectedSubject: String,
        selectedChapter: String,
        scope: String,
        specificTopicText: String
    ): GenerationPayload {
        val trimmedTopic = specificTopicText.trim()
        val specificTopic = if (scope.equals("specificTopic", ignoreCase = true) || scope.equals("Specific Topic", ignoreCase = true)) {
            if (trimmedTopic.isNotEmpty()) trimmedTopic else null
        } else {
            null
        }

        return GenerationPayload(
            className = selectedClass,
            subject = selectedSubject,
            chapter = selectedChapter,
            scope = if (scope.contains("Specific", ignoreCase = true)) "specificTopic" else "entireChapter",
            specificTopic = specificTopic
        )
    }
}
