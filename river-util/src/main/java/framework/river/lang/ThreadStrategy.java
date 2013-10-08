package framework.river.lang;

/**
 * An enumeration for capturing how a class is kept thread safe.
 */
public enum ThreadStrategy {

    BLOCKING, CPU_SPIN, LOCKLESS, NOT_THREAD_SAFE

}
