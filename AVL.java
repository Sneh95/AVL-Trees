import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;

/**
 * My AVL implementation.
 *
 * @author Sneh Munshi
 */
public class AVL<T extends Comparable<T>> implements AVLInterface<T>,
       Gradable<T> {

    // Do not add additional instance variables
    private Node<T> root;
    private int size;

    @Override
    public void add(T data) {
        if (data == null) {
            throw new IllegalArgumentException();
        }
        root = addRecursion(root, data);
        size++;
    }

    /**
     * This is a private helper method used to add recursively to the tree
     *
     * @param current The current node which we are attempting to add to
     * @param data The data that needs to be added to the tree as a node
     */
    private Node<T> addRecursion(Node<T> current, T data) {
        if (current == null) {
            current = new Node<T>(data);
            //found the place to be added.
        } else {
            int val = data.compareTo(current.getData());
            if (val < 0) {
                current.setLeft(addRecursion(current.getLeft(), data));
            } else if (val == 0) {
                size--;
            } else if (val > 0) {
                current.setRight(addRecursion(current.getRight(), data));
            } else {
                return current;
            }
        }
            //this should pass current to rotate that checks if
            //the subtree with root current is balanced.
            //if it is not, it should rotate it and send back the root
            //of the balanced subtree.
        current = updateNode(current);
        return rotate(current);
    }

    /**
     * This is a private helper method used to perform rotations
     *
     * @param node The node which is to be rotated
     */
    private Node<T> rotate(Node<T> node) {
        if (node == null) {
            return null;
        }
        //node = updateNode(node);
        if (node.getBalanceFactor() > 1) {
            //This means it is left-heavy
            //Node<T> left = updateNode(node.getLeft());
            Node<T> left = node.getLeft();
            if (left.getBalanceFactor() >= 0) {
                node = rightRotate(node);
                //left child is left heavy
            } else {
                //left child is right heavy
                node = leftRightRotate(node);
            }
        } else if (node.getBalanceFactor() < -1) {
            //It is right heavy
            //Node<T> right = updateNode(node.getRight());
            Node<T> right = node.getRight();
            if (right.getBalanceFactor() <= 0) {
                node = leftRotate(node);
                //right child is right heavy
            } else {
                //right child is left heavy
                node = rightLeftRotate(node);
            }
        }
        node = updateNode(node);
        return node;
    }

    /**
     * This is a private helper method used to perform right rotation
     *
     * @param node The node which is to be rotated
     */
    private Node<T> rightRotate(Node<T> node) {
        Node<T> left = node.getLeft();
        node.setLeft(left.getRight());
        left.setRight(node);
        updateNode(node);
        updateNode(left);
        return left;
    }

    /**
     * This is a private helper method used to perform left rotation
     *
     * @param node The node which is to be rotated
     */
    private Node<T> leftRotate(Node<T> node) {
        Node<T> right = node.getRight();
        node.setRight(right.getLeft());
        right.setLeft(node);
        updateNode(node);
        updateNode(right);
        return right;
    }

    /**
     * This is a private helper method used to rotate right and left
     *
     * @param node The node which is to be rotated
     */
    private Node<T> rightLeftRotate(Node<T> node) {
        //first rotate right then rotate left
        //zig zag pattern
        Node<T> right = node.getRight();
        node.setRight(rightRotate(right));
        return leftRotate(node);
    }

    /**
     * This is a private helper method used to rotate left and right
     *
     * @param node The node which is to be rotated
     */
    private Node<T> leftRightRotate(Node<T> node) {
        //first rotate left and then right
        Node<T> left = node.getLeft();
        node.setLeft(leftRotate(left));
        return rightRotate(node);
    }

    /**
     * This is a private helper method used to update the
     * the fields of the node like it's height and balance
     * factor
     * @param node The node which is to be updated
     * @return The updated node
     */
    private Node<T> updateNode(Node<T> node) {
        if (node != null) {
            int leftHeight = 0;
            int rightHeight = 0;
            if (node.getLeft() != null) {
                leftHeight = node.getLeft().getHeight();
            } else {
                leftHeight = -1;
            }
            if (node.getRight() != null) {
                rightHeight = node.getRight().getHeight();
            } else {
                rightHeight = -1;
            }
            node.setHeight(1 + Math.max(leftHeight, rightHeight));
            node.setBalanceFactor(leftHeight - rightHeight);
        }
        return node;
    }


    @Override
    public T remove(T data) {
        if (data == null) {
            throw new IllegalArgumentException();
        }
        Node<T> toReturn = new Node<T>(null);
        root = removeRec(root, data, toReturn);
        return toReturn.getData();
    }

    /**
     * This is a private helper method used to remove recursively from the tree
     *
     * @param current The current node which we are attempting to remove from
     * @param data The data that needs to be removed from the tree
     */
    private Node<T> removeRec(Node<T> curr, T data, Node<T> toReturn) {
        if (curr == null) {
            return null;
        }
        if (data.compareTo(curr.getData()) < 0) {
            curr.setLeft(removeRec(curr.getLeft(), data, toReturn));
        } else if (data.compareTo(curr.getData()) > 0) {
            curr.setRight(removeRec(curr.getRight(), data, toReturn));
        } else {
            //we found the node to remove so decrement size.
            size--;
            toReturn.setData(curr.getData());
            if (curr.getLeft() != null && curr.getRight() != null) {
                curr.setData(successor(curr));
                //go down the path of the successor and update those children
                //Since this does not re-traverse the entire tree, the big-O is
                //preserved.
                if (curr.getRight() != null) {
                    curr.getRight().setLeft(updateChildren(
                        curr.getRight().getLeft()));
                }
                curr.setRight(updateNode(curr.getRight()));
            } else if (curr.getLeft() == null) {
                curr = curr.getRight();
            } else {
                curr = curr.getLeft();
            }
        }
        curr = updateNode(curr);
        return rotate(curr);
    }
    /**
     * Once we remove a node, all of its children have to get
     * their heights updated. This is a method to perform that recursively.
     * @param curr
     * @return
     */
    private Node<T> updateChildren(Node<T> curr) {
        if (curr == null) {
            return null;
        }
        curr = updateNode(curr);
        curr.setLeft(updateChildren(curr.getLeft()));
        curr.setRight(updateChildren(curr.getRight()));
        return curr;
    }
    /**
     * This is a private helper method in case of having two children
     *
     * @param current The current node
     * @return The successor to the node
     */
    private T successor(Node<T> current) {
        Node<T> suc = current.getRight();
        Node<T> successorParent = null;
        while (suc.getLeft() != null) {
            successorParent = suc;
            suc = suc.getLeft();
            //getting the leftmost node
        }
        T newVal = suc.getData();
        if (successorParent == null) {
            current.setRight(suc.getRight());
        } else {
            //parent of the one we took from bottom gets to make
            //its left child the right child of the node that was
            //taken away
            successorParent.setLeft(suc.getRight());
        }
        return newVal;
    }

    @Override
    public T get(T data) {
        if (data == null) {
            throw new IllegalArgumentException();
        }
        Node<T> required = getNode(root, data);
        if (required != null) {
            return required.getData();
        }
        return null;
    }

    /**
     * This is a private helper method to get the required node using recursion.
     * @param current
     * @param data
     * @return The required node that we were attempting to get
     */
    private Node<T> getNode(Node<T> current, T data) {
        if (current == null) {
            return null;
        }
        if (current.getData().equals(data)) {
            return current;
        } else if (data.compareTo(current.getData()) < 0) {
            return getNode(current.getLeft(), data);
        } else {
            return getNode(current.getRight(), data);
        }
    }

    @Override
    public boolean contains(T data) {
        if (data == null) {
            throw new IllegalArgumentException();
        }
        return !(get(data) == null);
    }

    @Override
    public boolean isEmpty() {
        return (root == null);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<T> preorder() {
        ArrayList<T> list = new ArrayList<T>();
        if (root == null) {
            return list;
        }
        preorderRec(root, list);
        return list;
    }

    /**
     * This is a private helper method to traverse the tree in preprder and
     * add it to a list.
     * @param current
     * @param aList
     */
    private void preorderRec(Node<T> current, ArrayList<T> aList) {
        if (current == null) {
            return;
        }
        aList.add(current.getData());
        preorderRec(current.getLeft(), aList);
        preorderRec(current.getRight(), aList);
    }

    @Override
    public List<T> postorder() {
        ArrayList<T> list = new ArrayList<T>();
        if (root == null) {
            return list;
        }
        postorderRec(root, list);
        return list;
    }

    /**
     * This is a private helper method that traverses the tree in
     * postorder and adds it to a list.
     * @param current
     * @param aList
     */
    private void postorderRec(Node<T> current, ArrayList<T> aList) {
        if (current == null) {
            return;
        }
        postorderRec(current.getLeft(), aList);
        postorderRec(current.getRight(), aList);
        aList.add(current.getData());
    }

    @Override
    public List<T> inorder() {
        ArrayList<T> list = new ArrayList<T>();
        if (root == null) {
            return list;
        }
        inorderRec(root, list);
        return list;
    }

    /**
     * This is a private helper method that traverses the tree
     * inorder and adds it to a list.
     * @param current
     * @param aList
     */
    private void inorderRec(Node<T> current, ArrayList<T> aList) {
        if (current == null) {
            return;
        }
        inorderRec(current.getLeft(), aList);
        aList.add(current.getData());
        inorderRec(current.getRight(), aList);
    }

    @Override
    public List<T> levelorder() {
        ArrayList<T> aList = new ArrayList<T>();
        if (root == null) {
            return aList;
        }
        Queue<Node<T>> queue = new LinkedList<Node<T>>();
        Node<T> current = root;
        queue.add(current);
        while (!queue.isEmpty()) {
            current = queue.poll();
            aList.add(current.getData());
            if (current.getLeft() != null) {
                queue.add(current.getLeft());
            }
            if (current.getRight() != null) {
                queue.add(current.getRight());
            }
        }
        return aList;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public int height() {
        if (root == null) {
            return -1;
        }
        return root.getHeight();
    }

    @Override
    public Node<T> getRoot() {
        return root;
    }
}