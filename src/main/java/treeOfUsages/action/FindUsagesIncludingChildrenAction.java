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
            "Build Tree of Usages (include immediate child methods)",
            "Build tree of usages (include immediate child methods)",
            AllIcons.General.OverridenMethod,
            false,
            true
        );
    }
}