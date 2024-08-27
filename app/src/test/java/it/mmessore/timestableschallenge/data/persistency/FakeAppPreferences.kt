package it.mmessore.timestableschallenge.data.persistency

class FakeAppPreferences(
    override var numQuestions: Int = 20,
    override var minTable: Int = 1,
    override var maxTable: Int = 9,
    override var playSounds: Boolean = true,
    override var extendedMode: Boolean = false,
    override var autoConfirm: Boolean = false,
    override var overwriteBestScores: Boolean = false,
    override var useTimeLeft: Boolean = true,
    override var themeStyle: AppPreferences.AppThemeStyle = AppPreferences.AppThemeStyle.LIGHT,
) : AppPreferences
