package entities.core;

import java.util.HashSet;

public class IntHashSet extends HashSet<Integer> implements Comparable<IntHashSet> {
    @Override
    public int compareTo(IntHashSet o) {
        Integer size1 = this.size(),
                size2 = o.size();
        return size2.compareTo(size1);
    }
}
