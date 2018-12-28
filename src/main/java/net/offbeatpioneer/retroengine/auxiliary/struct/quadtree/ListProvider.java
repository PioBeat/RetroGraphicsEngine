package net.offbeatpioneer.retroengine.auxiliary.struct.quadtree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Paavo Toivanen https://github.com/pvto
 */
public interface ListProvider<T> {
    
    
    <T> List<T> getList(int expectedListSize, int treeCurrentSize);

    
    
    
    
    ListProvider LP_LINKEDLIST = new ListProvider() {
        public List getList(int expectedListSize, int treeCurrentSize)
        {
            return new LinkedList();
        }
    };
    
    ListProvider LP_ARRAYLIST = new ListProvider() {
        public List getList(int expectedListSize, int treeCurrentSize)
        {
            return new ArrayList(expectedListSize);
        }
    };
}

