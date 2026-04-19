package it.mmessore.timestableschallenge.ui.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class Navigator(val state: NavigationState){
    fun navigate(route: NavKey){
        if (route in state.backStacks.keys){
            // This is a top level route, just switch to it.
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun goBack(){
        val currentStack = state.backStacks[state.topLevelRoute] ?:
        error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        // If we're at the base of the current route, go back to the start route stack.
        if (currentRoute == state.topLevelRoute){
            if (state.topLevelRoute != state.startRoute) {
                state.topLevelRoute = state.startRoute
            }
        } else {
            currentStack.removeLastOrNull()
        }
    }

    /**
     * Navigates back and then to a new route.
     * Useful for inclusive popUpTo equivalent.
     */
    fun popAndNavigate(route: NavKey) {
        val currentStack = state.backStacks[state.topLevelRoute] ?:
        error("Stack for ${state.topLevelRoute} not found")
        currentStack.removeLastOrNull()
        navigate(route)
    }

    /**
     * Navigates to a new route while clearing the current stack up to the top level route.
     */
    fun navigateAndPopUpToRoot(route: NavKey) {
        val currentStack = state.backStacks[state.topLevelRoute] ?:
        error("Stack for ${state.topLevelRoute} not found")
        while (currentStack.size > 1) {
            currentStack.removeLastOrNull()
        }
        navigate(route)
    }
}
