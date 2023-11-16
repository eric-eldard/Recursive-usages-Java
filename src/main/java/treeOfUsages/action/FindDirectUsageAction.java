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
            "Build Tree of Usages for method",
            "Build tree of usages for method",
            AllIcons.Nodes.Method,
            false,
            false
        );
    }
}
