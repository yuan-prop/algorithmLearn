package zuo.class10;

import java.util.*;


/**
 * 最小生成树算法之Kruskal贪心算法(利用并查集)
 * 1 总是从权值最小的边开始考虑，依次考虑权值依次变大的边
 * 2 当前的边要么进入最小生成树的集合，要么丢弃
 * 3 如果当前的边进入最小生成树的集合中不会形成环，就要当前边
 * 4 如果当前的边进入最小生成树的集合中形成环，就不要当前边
 * 5考察完所有边之后，最小生成树的集合也得到了
 */
public class Code04_Kruskal {

    // Union-Find Set(并查集)
    public static class UnionFind{
        //key 某一个节点，value key节点往上的代表节点
        private HashMap<Node, Node> fatherMap;

        // key 某一个集合的代表节点，value：key所在集合的节点个数
        private HashMap<Node, Integer> sizeMap;


        public UnionFind(){
            fatherMap = new HashMap<Node, Node>();
            sizeMap = new HashMap<Node, Integer>();
        }

        public void makeSets(Collection<Node> nodes){
            fatherMap.clear();
            sizeMap.clear();
            for(Node n : nodes){
                fatherMap.put(n, n);
                sizeMap.put(n, 1);
            }
        }

        // 寻找当前节点所在集合的代表节点
        private Node findFather(Node n){
            Stack<Node> path = new Stack<>();
            while(n != fatherMap.get(n)){
                path.add(n);
                n = fatherMap.get(n);
            }
            while(!path.isEmpty()){
                // 更新沿途节点的代表节点
                fatherMap.put(path.pop(), n);
            }
            return n;
        }

        public boolean isSameSet(Node a, Node b){
            return fatherMap.get(a) == fatherMap.get(b);
        }

        // 将a或b节点作为另一个的代表节点
        public void union(Node a, Node b){
            if(a == null || b == null){
                return;
            }
            Node aDai = findFather(a);
            Node bDai = findFather(b);
            if(aDai != bDai){
                Integer aSetSize = sizeMap.get(aDai);
                Integer bSetSize = sizeMap.get(bDai);
                if(aSetSize >= bSetSize){
                    fatherMap.put(bDai, aDai);
                    sizeMap.put(aDai, aSetSize + bSetSize);
                    sizeMap.remove(bDai);
                }else{
                    fatherMap.put(aDai, bDai);
                    sizeMap.put(bDai, bSetSize + aSetSize);
                    sizeMap.remove(aDai);
                }
            }
        }
    }

    public static class EdgeComparator implements Comparator<Edge>{

        @Override
        public int compare(Edge o1, Edge o2) {
            return o1.weight - o2.weight;
        }
    }

    /**
     * Kruskal主方法: 求最小生成树（在一个图中找出连通所有节点且边权之和最小的边对应的连通子图）
     */
    public static Set<Edge> KruskalMST(Graph gragh){
        UnionFind uf = new UnionFind();
        uf.makeSets(gragh.nodes.values());
        // 从小边到大边，依次弹出，（小根堆）
        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>(new EdgeComparator());
        for(Edge edge : gragh.edges){// M 条边
            priorityQueue.add(edge); // O(logM)
        }
        Set<Edge> result = new HashSet<>();
        while(!priorityQueue.isEmpty()){
            Edge edge = priorityQueue.poll();
            // 判断不在同一个集合才行，防止形成环
            if(!uf.isSameSet(edge.from, edge.to)){
                result.add(edge);
                uf.union(edge.from, edge.to);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Node a = new Node('a');
        Node b = new Node('b');
        Node c = new Node('c');
        Node d = new Node('d');
        Node e = new Node('e');

        Edge ab = new Edge(1, a, b);
        Edge ac = new Edge(6, a, c);
        Edge ad = new Edge(4, a, d);
        Edge bc = new Edge(1, b, c);
        Edge be = new Edge(6, b, e);
        Edge ce = new Edge(2, c, e);
        Edge cd = new Edge(1, c, d);
        Edge de = new Edge(5, d, e);

        a.edges.add(ab);
        a.edges.add(ac);
        a.edges.add(ad);
        b.edges.add(bc);
        b.edges.add(be);
        c.edges.add(cd);
        c.edges.add(ce);
        d.edges.add(de);

        Graph graph = new Graph();
        graph.nodes.put(1, a);
        graph.nodes.put(2, b);
        graph.nodes.put(3, c);
        graph.nodes.put(4, d);
        graph.nodes.put(5, e);

        graph.edges.addAll(Arrays.asList(ab,ac,ad,bc,be,ce,cd,de));

        Set<Edge> edges = KruskalMST(graph);
        for(Edge edge : edges) {
            System.out.println((char)edge.from.value + "->" +(char)edge.to.value + "    " + edge.weight);
        }
    }

}
