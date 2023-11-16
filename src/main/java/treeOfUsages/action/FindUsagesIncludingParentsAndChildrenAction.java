package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import treeOfUsages.Plugin;

/**
 * Find usages of method, including usages of overridden parent class methods and/or implemented interface methods and
 * of shallow child methods. This has the highest potential time complexity of all find-usages actions.
 */
public class FindUsagesIncludingParentsAndChildrenAction extends FindUsagesAction
{
    @SuppressWarnings("unused")
    public FindUsagesIncludingParentsAndChildrenAction()
    {
    }

    public FindUsagesIncludingParentsAndChildrenAction(Plugin plugin)
    {
        super(
            plugin,
            "Build Tree of Usages (include immediate parent and child methods)",
            "Build tree of usages (include immediate parent and child methods)",
            AllIcons.Gutter.SiblingInheritedMethod,
            true,
            true
        );
    }
}