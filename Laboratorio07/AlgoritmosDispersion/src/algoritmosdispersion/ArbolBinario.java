package algoritmosdispersion;

import java.util.function.BiConsumer;

/**
 * Árbol binario de búsqueda genérico (ABB) sencillo.
 * @param <K> tipo de la clave, debe ser comparable
 * @param <V> tipo del valor asociado
 */
public class ArbolBinario<K extends Comparable<K>, V> {

    /** Nodo interno del árbol */
    private class Nodo {
        K clave;
        V valor;
        Nodo izq, der;
        Nodo(K c, V v) { clave = c; valor = v; }
    }

    private Nodo raiz;

    /* ---------------------------------------------------------------------
     * Operaciones básicas
     * ------------------------------------------------------------------ */

    /** Inserta un par (clave, valor) o actualiza si la clave existe. */
    public void insertar(K clave, V valor) {
        raiz = insertar(raiz, clave, valor);
    }

    private Nodo insertar(Nodo n, K clave, V valor) {
        if (n == null) return new Nodo(clave, valor);
        int cmp = clave.compareTo(n.clave);
        if (cmp < 0)      n.izq = insertar(n.izq, clave, valor);
        else if (cmp > 0) n.der = insertar(n.der, clave, valor);
        else              n.valor = valor; // actualiza
        return n;
    }

    /** Devuelve el valor asociado a la clave o {@code null} si no existe. */
    public V buscar(K clave) {
        Nodo n = raiz;
        while (n != null) {
            int cmp = clave.compareTo(n.clave);
            if (cmp == 0) return n.valor;
            n = (cmp < 0) ? n.izq : n.der;
        }
        return null;
    }

    /** Indica si el árbol contiene la clave. */
    public boolean contiene(K clave) { return buscar(clave) != null; }

    /* ---------------------------------------------------------------------
     * Recorridos y utilidades
     * ------------------------------------------------------------------ */

    /**
     * Recorre el árbol en orden y aplica la acción a cada (clave, valor).
     * Se usa para re‑inserción durante el re‑hash en la tabla encadenada.
     */
    public void forEach(BiConsumer<K, V> action) {
        inOrder(raiz, action);
    }

    private void inOrder(Nodo n, BiConsumer<K, V> action) {
        if (n == null) return;
        inOrder(n.izq, action);
        action.accept(n.clave, n.valor);
        inOrder(n.der, action);
    }

    /** Altura del árbol (longitud del camino más largo desde la raíz) */
    public int altura() { return altura(raiz); }
    private int altura(Nodo n) {
        if (n == null) return 0;
        return 1 + Math.max(altura(n.izq), altura(n.der));
    }

    /** Número total de nodos del árbol */
    public int size() { return size(raiz); }
    private int size(Nodo n) {
        if (n == null) return 0;
        return 1 + size(n.izq) + size(n.der);
    }
}
