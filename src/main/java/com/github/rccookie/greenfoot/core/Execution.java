package com.github.rccookie.greenfoot.core;

import com.github.rccookie.greenfoot.java.util.concurrent.Future;
import com.github.rccookie.greenfoot.java.util.concurrent.FutureImpl;
import com.github.rccookie.util.Console;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class Execution {

    private static final Set<ExecutionTask<Object>> TASKS = new HashSet<>();
    private static final Set<RepeatingExecutionTask> REPEATING_TASKS = new HashSet<>();

    static {
        Core.registerOnEarlyGlobalUpdate(Execution::runTasks);
        Core.registerOnGlobalUpdate(Execution::runTasks);
        repeat(() -> Console.custom("fps", Time.stableFps()), 1, 1);
    }



    @SuppressWarnings("unchecked")
    private static void runTasks() {
        for(ExecutionTask<Object> task : TASKS.toArray(new ExecutionTask[0])) {
            if(task.result.isCancelled())
                TASKS.remove(task);
            else if(task.requirement.getAsBoolean()) {
                task.result.setValue(task.action.get());
                TASKS.remove(task);
            }
        }
        for(RepeatingExecutionTask task : REPEATING_TASKS.toArray(new RepeatingExecutionTask[0])) {
            if(task.requirement.getAsBoolean()) {
                if(!task.action.getAsBoolean())
                    REPEATING_TASKS.remove(task);
            }
        }
    }



    public static void repeat(Runnable task, double delay) {
        repeat(task, delay, 0);
    }

    public static void repeat(Runnable task, double delay, double initialDelay) {
        repeat(() -> {
            task.run();
            return true;
        }, delay, initialDelay);
    }

    public static void repeat(BooleanSupplier task, double delay) {
        repeat(task, delay, 0);
    }

    public static void repeat(BooleanSupplier task, double delay, double initialDelay) {
        REPEATING_TASKS.add(new RepeatingExecutionTask(task, new BooleanSupplier() {
            double nextTime = Time.seconds() + initialDelay;
            @Override
            public boolean getAsBoolean() {
                if(Time.seconds() < nextTime) return false;
                nextTime += delay;
                return true;
            }
        }));
    }

    public static void runLater(Runnable task) {
        runLater(task, 0);
    }

    public static void runLater(Runnable task, double delay) {
        runLater(() -> {
            task.run();
            return null;
        }, delay);
    }



    public static <R> Future<R> runLater(Supplier<R> task) {
        return runWhen(task, () -> true);
    }

    public static <R> Future<R> runLater(Supplier<R> task, double delay) {
        double executionTime = Time.seconds() + delay;
        return runWhen(task, () -> Time.seconds() >= executionTime);
    }

    @SuppressWarnings("unchecked")
    public static <R> Future<R> runWhen(Supplier<R> task, BooleanSupplier requirement) {
        ExecutionTask<R> executionTask = new ExecutionTask<>(task, requirement);
        TASKS.add((ExecutionTask<Object>) executionTask);
        return executionTask.result;
    }



    private Execution() {
        throw new UnsupportedOperationException();
    }

    static void init() { }


    private static final class ExecutionTask<T> {

        public final Supplier<T> action;
        public final BooleanSupplier requirement;
        public final FutureImpl<T> result = new FutureImpl<>();

        private ExecutionTask(Supplier<T> action, BooleanSupplier requirement) {
            this.action = action;
            this.requirement = requirement;
        }
    }

    private static final class RepeatingExecutionTask {
        public final BooleanSupplier action;
        public final BooleanSupplier requirement;

        private RepeatingExecutionTask(BooleanSupplier action, BooleanSupplier requirement) {
            this.action = action;
            this.requirement = requirement;
        }
    }
}
