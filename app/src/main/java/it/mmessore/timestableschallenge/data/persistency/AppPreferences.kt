package it.mmessore.timestableschallenge.data.persistency

interface AppPreferences {
    var numQuestions: Int
    var minTable: Int
    var maxTable: Int
    var playSounds: Boolean
    var extendedMode: Boolean
    var overwriteBestScores: Boolean
    var useTimeLeft: Boolean
}