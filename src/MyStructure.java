import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;


interface IMyStructure {
    // zwraca węzeł o podanym kodzie lub null
    INode findByCode(String code);

    // zwraca węzeł o podanym rendererze lub null
    INode findByRenderer(String renderer);

    //zwraca liczbę węzłów
    int count();
}


public class MyStructure implements IMyStructure {

    public static void main(String[] args) {
        Node node1 = new Node("code1", "renderer1");
        Node node2 = new Node("code2", "renderer2");
        System.out.println(node1.countChilds());   //result=1

        CompositeNode compositeNode = new CompositeNode("compositeNodeCode1", "compositeNodeRenderer1");
        compositeNode.add(node1);
        compositeNode.add(node2);

        CompositeNode compositeNode2 = new CompositeNode("compositeNodeCode2", "compositeNodeRenderer2");
        Node node3 = new Node("code3", "renderer3");
        compositeNode2.add(node1);
        compositeNode2.add(node3);

        compositeNode.add(compositeNode2);

        MyStructure myStructure = new MyStructure();
        myStructure.nodes.add(node1);
        myStructure.nodes.add(node2);
        myStructure.nodes.add(compositeNode);
        System.out.println(myStructure);
        System.out.println(myStructure.count());   //Calculates Nodes and CompositeNodes! result=8
        System.out.println(myStructure.findByCode("compositeNodeCode2"));
        System.out.println(myStructure.findByRenderer("compositeNodeRenderer2"));
        System.out.println(myStructure.findByCode("code3"));
        System.out.println(myStructure.findByRenderer("renderer3"));
        System.out.println(myStructure.findByCode(""));   //result=null
    }

    private List<INode> nodes = new LinkedList<>();

    @Override
    public INode findByCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code is null!");
        }
        /**since Predicate is functional interface we can use lambda to make code shorter**/
        return findByPredicate(c -> c.getCode().equals(code));
    }

    @Override
    public INode findByRenderer(String renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("Renderer is null!");
        }
        /**since Predicate is functional interface we can use lambda to make code shorter**/
        return findByPredicate(r -> r.getRenderer().equals(renderer));
    }

    /**
     * DRY - we write method findByPredicate().which takes Predicate as an argument, because methods findByCode() and findByRenderer are similar.
     * findByPredicate() - converts list to a stream, straights a nested structure and searches for element, which meets the condition of Predicate by means of Predicate functional Interface.
     * uses nodeToStream() method to have access to nested nodes
     **/
    private INode findByPredicate(Predicate<INode> node) {
        return nodes.stream()
                .flatMap(INode::nodeToStream)
                .filter(node)
                .findFirst()
                .orElse(null);
    }

    /**applies countChilds() to every element of stream(node or composite node) and calculates the sum of elements**/
    @Override
    public int count() {
        return nodes
                .stream()
                .mapToInt(INode::countChilds)
                .sum();
    }

    public void add(INode node) {
        nodes.add(node);
    }

    @Override
    public String toString() {
        return "MyStructure{" +
                "nodes=" + nodes +
                '}';
    }
}


class Node implements INode {

    private String code;
    private String renderer;

    public Node(String code, String renderer) {
        this.code = code;
        this.renderer = renderer;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getRenderer() {
        return renderer;
    }

    @Override
    public Stream<INode> nodeToStream() {
        return Stream.of(this);
    }

    @Override
    public String toString() {
        return "Node{" +
                "code='" + code + '\'' +
                ", renderer='" + renderer + '\'' +
                '}';
    }
}


interface INode {
    String getCode();

    String getRenderer();

    /**by default value of ordinary node is 1**/
    default int countChilds() {
        return 1;
    }

    /**changes node to stream**/
    Stream<INode> nodeToStream();
}


interface ICompositeNode extends INode {
    List<INode> getNodes();

    /**iterates by childs and calculates sum of Nodes and CompositeNodes, accessing nested nodes by means of recursion**/
    @Override
    default int countChilds() {
        return getNodes()
                .stream()
                .mapToInt(INode::countChilds)
                .sum() + 1;
    }
}


class CompositeNode implements ICompositeNode {

    List<INode> nodes = new LinkedList<>();
    private String code;
    private String renderer;

    public CompositeNode(String code, String renderer) {
        this.code = code;
        this.renderer = renderer;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getRenderer() {
        return renderer;
    }

    /**handles nodes and composite nodes, changes node and all the nested nodes to a stream, using recursion, flatMap - straights a nested structure**/
    @Override
    public Stream<INode> nodeToStream() {
        return Stream.concat(
                Stream.of(this),
                nodes.stream().flatMap(INode::nodeToStream));
    }

    @Override
    public List<INode> getNodes() {
        return nodes;
    }

    public void add(INode node) {
        nodes.add(node);
    }

    @Override
    public String toString() {
        return "CompositeNode{" +
                "list=" + nodes +
                ", code='" + code + '\'' +
                ", renderer='" + renderer + '\'' +
                '}';
    }
}

