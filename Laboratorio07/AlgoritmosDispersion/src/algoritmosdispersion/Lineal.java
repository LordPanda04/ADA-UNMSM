package algoritmosdispersion;

import java.util.Arrays;

/**
 * Tabla hash con direccionamiento abierto (prueba lineal).
 * @param <K> tipo de la clave
 * @param <V> tipo del valor
 */
public class Lineal<K, V> {

    /* ---------------- nodo interno ---------------- */
    private static class Entrada<K, V> {
        K clave;
        V valor;
        Entrada(K c, V v) { clave = c; valor = v; }
    }

    /* ---------------- campos ---------------- */
    private Entrada<K, V>[] tabla;
    private int tamanio;
    private static final int CAPACIDAD_DEFECTO = 11;
    private static final double FACTOR_CARGA = 0.70;

    @SuppressWarnings("unchecked")
    public Lineal() {
        tabla = (Entrada<K, V>[]) new Entrada[CAPACIDAD_DEFECTO];
        tamanio = 0;
    }

    /* ---------------- API pública ---------------- */

    /** Inserta o actualiza una clave. */
    public void put(K clave, V valor) {
        if ((double) (tamanio + 1) / tabla.length > FACTOR_CARGA) rehash();

        int idx = encontrarSlot(clave, true);
        if (tabla[idx] == null) tamanio++;          // nueva clave
        tabla[idx] = new Entrada<>(clave, valor);  // inserta / actualiza
    }

    /** Devuelve el valor asociado o null si no existe. */
    public V get(K clave) {
        int idx = encontrarSlot(clave, false);
        return idx == -1 ? null : tabla[idx].valor;
    }

    /** Elimina una clave y devuelve su valor o null. */
    public V remove(K clave) {
        int idx = encontrarSlot(clave, false);
        if (idx == -1) return null;
        V antiguo = tabla[idx].valor;
        tabla[idx] = null;
        tamanio--;
        reubicarCluster(idx);
        return antiguo;
    }

    /** Devuelve el número de elementos almacenados. */
    public int size() { return tamanio; }

    /** Comprueba si existe la clave. */
    public boolean contiene(K clave) { return get(clave) != null; }

    /* ---------------- helpers internos ---------------- */

    /** Encuentra índice de la clave o el primer hueco libre. */
    private int encontrarSlot(K clave, boolean paraInsertar) {
        int idx = Math.floorMod(clave.hashCode(), tabla.length);
        int pasos = 0;
        while (tabla[idx] != null && pasos < tabla.length) {
            if (tabla[idx].clave.equals(clave)) return idx;   // encontrado
            idx = (idx + 1) % tabla.length;                   // prueba lineal
            pasos++;
        }
        return paraInsertar ? idx : -1;  // si no está y es búsqueda, devuelve -1
    }

    /** Rehash -> duplica tamaño a siguiente primo y reinsertar */
    @SuppressWarnings("unchecked")
    private void rehash() {
        Entrada<K, V>[] vieja = tabla;
        tabla = (Entrada<K, V>[]) new Entrada[siguientePrimo(vieja.length * 2)];
        tamanio = 0;
        for (Entrada<K, V> e : vieja) if (e != null) put(e.clave, e.valor);
    }

    /** Reubica entradas del clúster roto tras un remove. */
    private void reubicarCluster(int hueco) {
        int idx = (hueco + 1) % tabla.length;
        while (tabla[idx] != null) {
            Entrada<K, V> temp = tabla[idx];
            tabla[idx] = null;
            tamanio--;
            put(temp.clave, temp.valor);
            idx = (idx + 1) % tabla.length;
        }
    }

    private int siguientePrimo(int n) { while (!esPrimo(n)) n++; return n; }
    private boolean esPrimo(int n) {
        if (n < 2) return false;
        if (n % 2 == 0 && n != 2) return false;
        for (int i = 3; i * i <= n; i += 2)
            if (n % i == 0) return false;
        return true;
    }

    @Override
    public String toString() { return Arrays.toString(tabla); }
}
