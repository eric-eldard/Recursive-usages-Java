package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import treeOfUsages.Plugin;
import treeOfUsages.ProjectIcons;

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
            "Build Tree (include immediate parent and child methods)",
            "Build a tree of usages which includes both immediate parent and child methods",
            ProjectIcons.SiblingMethod,
            true,
            true
        );
    }
}