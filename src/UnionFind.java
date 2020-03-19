/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Arrays;

/**
 *
 * @author Travis Bonneau
 */
public class UnionFind {

    private int[] id;
    private int count;
    
    public UnionFind(int n) {
        id = new int[n];
        count = n;
        for(int i=0; i<n; i++){
            id[i] = i;
        }
    }
    
    public int count(){
        return count;
    }
    
    public void union(int i, int j){
        int iID = find(i);
        int jID = find(j);
        if(jID == iID)
            return;
        
        for(int r=0; r<id.length; r++){
            if(id[r]==jID)
                id[r] = iID;
        }
        count--;
    }
        
    public int find(int i) {
        validate(i);
        return id[i];
    }
    
    public boolean connected(int i, int j){
        return find(i)==find(j);
    }
    
    private void validate(int i){
        if(i<0 || i>=id.length)
            throw new ArrayIndexOutOfBoundsException();
    }
    
    public String toString(){
        return Arrays.toString(id);
    }
}
