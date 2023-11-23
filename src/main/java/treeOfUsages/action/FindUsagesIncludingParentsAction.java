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
            "Build Tree (include immediate parents)",
            "Build a tree of usages which includes methods the selected method directly overrides or implements",
            AllIcons.General.ImplementingMethod,
            true,
            false
        );
    }
}