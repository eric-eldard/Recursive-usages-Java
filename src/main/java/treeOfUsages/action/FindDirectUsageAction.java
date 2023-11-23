package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import treeOfUsages.Plugin;

/**
 * Find usages of method without including usages of overridden parent/interface or child methods
 */
public class FindDirectUsageAction extends FindUsagesAction
{
    @SuppressWarnings("unused")
    public FindDirectUsageAction()
    {
    }

    public FindDirectUsageAction(Plugin plugin)
    {
        super(
            plugin,
            "Build Tree",
            "Build a tree of usages for the method at the cursor or the method in the current tree which has focus",
            AllIcons.Nodes.Method,
            false,
            false
        );
    }
}
