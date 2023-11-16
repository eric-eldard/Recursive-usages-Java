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
            "Build Tree of Usages (include immediate parent methods)",
            "Build tree of usages (include immediate parent methods)",
            AllIcons.General.ImplementingMethod,
            true,
            false
        );
    }
}