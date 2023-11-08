package myToolWindow.Nodes;

import com.intellij.psi.impl.source.PsiMethodImpl;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class ClassNodeSet {
    final HashSet<ClassNode> set = new HashSet<>();

    public boolean contains(PsiMethodImpl methodImpl) {
        for (ClassNode classNode : set) {
            if (classNode.getElement().equals(methodImpl)) {
                return true;
            }
        }

        return false;
    }

    public int size() {
        return set.size();
    }

    @Nullable
    public ClassNode find(PsiMethodImpl methodImpl) {
        for (ClassNode classNode : set) {
            if (classNode.getElement().equals(methodImpl)) {
                return classNode;
            }
        }

        return null;
    }

    public void add(ClassNode classNode) {
        set.add(classNode);
    }

    public void clear() {
        set.clear();
    }
}
