package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import treeOfUsages.Plugin;

/**
 * Find usages of method without including usages of overridden parent class methods or implemented interface methods
 */
public class FindUsagesExcludingParentsAction extends FindUsagesAction
{
    @SuppressWarnings("unused")
    public FindUsagesExcludingParentsAction()
    {
    }

    public FindUsagesExcludingParentsAction(Plugin plugin)
    {
        super(
            plugin,
            "Build a Tree of Usages",
            "Build a tree of usages",
            AllIcons.RunConfigurations.TestState.Run,
            false
        );
    }
}
