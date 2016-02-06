package alainvanhout.rest.request;

import java.util.*;

public class Path {

    private String step;
    private Queue<String> steps = new LinkedList<String>();

    public boolean hasNextStep() {
        return !steps.isEmpty();
    }

    public boolean done() {
        return steps.isEmpty();
    }

    public String nextStep() {
        step = steps.poll();
        return step;
    }


    public String peekStep() {
        step = steps.peek();
        return step;
    }

    public Queue<String> getSteps() {
        return steps;
    }

    public String getStep() {
        return step;
    }

    public Path steps(Queue<String> steps) {
        this.steps = steps;
        return this;
    }
}
