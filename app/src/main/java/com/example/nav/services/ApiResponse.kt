package com.example.nav.services

data class ApiResponse(
    val message: String,
    val results: Results,
    val code: Int
)

data class RegisterResponse(
    val message: String,
    val code: Int
)

data class VerifyResponse(
    val message: String,
    val code: Int
)

data class BadgesResponse(
    val reference_number: String,
    val user_id: Int,
    val badge_id: Int,
    val completed_at: String,
    val badge: Badge
)

data class Badge(
    val reference_number: String,
    val badge_name: String,
    val description: String,
    val badge_image: String
)

data class ProgrammingLanguage(
    val id: Int,
    val programming_language: String,
    val description: String,
    val reference_number: String,
    val getting_started: List<Steps>,
    val exams: List<Exam>,
    val chapters: List<Chapter>
)

data class Steps(
    val steps: List<StepsDetails>
)

data class StepsDetails(
    val getting_started_id: Int,
    val name: String,
    val description: String,
    val image: String,
    val order: Int
)

data class Exam(
    val reference_number: String,
    val programming_language_id: Int,
    val question_number: Int,
    val question: String,
    val code_snippet: String?,
    val choice_1: String,
    val choice_2: String,
    val choice_3: String,
    val choice_4: String,
    val correct_answer: Int
)
data class Chapter(
    val chapter_name: String,
    val reference_number: String,
    val lessons: List<Lesson>,
    val chapter_assessment: List<ChapterAssessment>
)

data class Lesson(
    val reference_number: String,
    val chapter_id: Int,
    val lesson_number: String,
    val lesson_title: String,
    val lesson_description: String,
    val lesson_video: String,
    val lesson_example_code: String,
    val lesson_output: String,
    val lesson_explanation: String
)

data class ChapterAssessment(
    val reference_number: String,
    val chapter_id: Int,
    val question_number: Int,
    val question: String,
    val code_snippet: String?,
    val choice_1: String,
    val choice_2: String,
    val choice_3: String,
    val choice_4: String,
    val correct_answer: Int
)