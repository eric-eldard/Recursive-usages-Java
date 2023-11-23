package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import treeOfUsages.Plugin;

/**
 * Find usages of method, including usages of shallow child methods
 */
public class FindUsagesIncludingChildrenAction extends FindUsagesAction
{
    @SuppressWarnings("unused")
    public FindUsagesIncludingChildrenAction()
    {
    }

    public FindUsagesIncludingChildrenAction(Plugin plugin)
    {
        super(
            plugin,
            "Build Tree (include immediate children)",
            "Build a tree of usages which includes shallow overrides of the selected method",
            AllIcons.General.OverridenMethod,
            false,
            true
        );
    }
}