#!/bin/bash
# Replaces { Text("xyz") } with { Text("xyz", color = Color(0xFF1E293B)) } inside DropdownMenuItem

sed -i 's/text = { Text("\([^"]*\)") }/text = { Text("\1", color = Color(0xFF1E293B)) }/g' app/src/main/java/com/example/ui/screens/bank/QuestionBankScreen.kt
sed -i 's/text = { Text("\([^"]*\)", fontSize = 14.sp) }/text = { Text("\1", fontSize = 14.sp, color = Color(0xFF1E293B)) }/g' app/src/main/java/com/example/ui/screens/generator/McqGeneratorSetupScreen.kt
sed -i 's/text = { Text("\([^"]*\)", fontSize = 14.sp) }/text = { Text("\1", fontSize = 14.sp, color = Color(0xFF1E293B)) }/g' app/src/main/java/com/example/ui/screens/mcq/GeneratedMcqViewScreen.kt
