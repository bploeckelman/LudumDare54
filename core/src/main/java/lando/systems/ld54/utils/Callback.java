package lando.systems.ld54.utils;

@FunctionalInterface
public interface Callback {
    void run(Object... params);
}
