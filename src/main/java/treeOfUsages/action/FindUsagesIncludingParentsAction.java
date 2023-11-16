package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import treeOfUsages.Plugin;

/**
 * Find usages of method, including usages of overridden parent class methods and/or implemented interface methods
 */
public class FindUsagesIncludingParentsAction extends FindUsagesAction
{
    @SuppressWarnings("unused")
    public FindUsagesIncludingParentsAction()
    {
    }

    public FindUsagesIncludingParentsAction(Plugin plugin)
    {
        super(
            plugin,
            "Build a Tree of Usages, including immediate parent methods",
            "Build a tree of usages, including immediate parent methods",
            AllIcons.RunConfigurations.TestState.Run_run,
            true
        );
    }
}