package de.dhbw.corona_world_app.ui.tools;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

//create own Pair Class it the other once do not allow setters
public class Pair<V,T> {
    public V first;
    public T second;

    public Pair(@NonNull V first,@NotNull T second) {
        this.first = first;
        this.second = second;
    }

    public void setFirst(V first) {
        this.first = first;
    }

    public void setSecond(T second) {
        this.second = second;
    }

    public static <A,B>  Pair<A,B>  create(A first,B second){
        return new Pair<>(first,second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return first.equals(pair.first) &&
                second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
