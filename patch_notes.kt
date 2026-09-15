import com.example.ui.components.CustomDropdownField

// inside the column
// remove the Box (lines 248-261)

// replace the FloatingCard content (lines 283-404) with:
                            Text(
                                text = "Select Class & Subject",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextOnWhitePrimary
                            )

                            CustomDropdownField(
                                label = "Class / Exam Target",
                                selectedValue = selectedClass,
                                options = classes,
                                onSelectOption = { newClass ->
                                    selectedClass = newClass
                                    val newSubjects = SyllabusData.syllabus[newClass]?.keys?.toList() ?: emptyList()
                                    selectedSubject = newSubjects.firstOrNull() ?: ""
                                    val newChapters = SyllabusData.syllabus[newClass]?.get(selectedSubject) ?: emptyList()
                                    selectedChapter = newChapters.firstOrNull() ?: ""
                                },
                                modifier = Modifier.testTag("notes_class_dropdown")
                            )

                            CustomDropdownField(
                                label = "Subject",
                                selectedValue = selectedSubject.ifEmpty { "Select Subject" },
                                options = subjects,
                                onSelectOption = { subj ->
                                    selectedSubject = subj
                                    val newChapters = SyllabusData.syllabus[selectedClass]?.get(subj) ?: emptyList()
                                    selectedChapter = newChapters.firstOrNull() ?: ""
                                },
                                modifier = Modifier.testTag("notes_subject_dropdown")
                            )

                            CustomDropdownField(
                                label = "Chapter / Unit",
                                selectedValue = selectedChapter.ifEmpty { "Select Chapter" },
                                options = chapters,
                                onSelectOption = { chap ->
                                    selectedChapter = chap
                                },
                                modifier = Modifier.testTag("notes_chapter_dropdown")
                            )

                            // Specific Topic / Keywords Input
                            OutlinedTextField(
                                value = specificTopic,
                                onValueChange = { specificTopic = it },
                                label = { Text("Specific Topic / Keywords (Optional)", fontSize = 12.sp) },
                                placeholder = { Text("e.g. Electromagnetic induction laws", fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VioletAccent,
                                    unfocusedBorderColor = CardBorderLight,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    focusedLabelColor = TextOnWhiteSecondary,
                                    unfocusedLabelColor = TextOnWhiteSecondary,
                                    focusedPlaceholderColor = TextOnWhiteSecondary,
                                    unfocusedPlaceholderColor = TextOnWhiteSecondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("notes_specific_topic_input"),
                                singleLine = true
                            )
