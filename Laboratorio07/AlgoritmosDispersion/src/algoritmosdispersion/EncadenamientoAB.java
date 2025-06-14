package algoritmosdispersion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer; //

/**
 * Tabla hash con encadenamiento (ABB por cubeta).
 * @param <K>
 * @param <V>
 */
public class EncadenamientoAB<K extends Comparable<K>, V> {

    private List<ArbolBinario<K, V>> tabla;
    private int capacidad;
    private int tamanio;
    private static final int CAPACIDAD_DEFECTO = 11;

    public EncadenamientoAB() {
        capacidad = CAPACIDAD_DEFECTO;
        initTabla();
    }

    private void initTabla() {
        tabla = new ArrayList<>(capacidad);
        for (int i = 0; i < capacidad; i++) tabla.add(new ArbolBinario<>());
        tamanio = 0;
    }

    public int size() { return tamanio; }

    public void put(K clave, V valor) {
        int idx = indice(clave);
        ArbolBinario<K, V> cubeta = tabla.get(idx);
        if (!cubeta.contiene(clave)) tamanio++;
        cubeta.insertar(clave, valor);
        if ((double) tamanio / capacidad > 1.2) rehash();
    }

    public V get(K clave) { return tabla.get(indice(clave)).buscar(clave); }

    public boolean contiene(K clave) { return tabla.get(indice(clave)).contiene(clave); }

    /* ---------- helpers ---------- */

    private int indice(K clave) { return Math.floorMod(clave.hashCode(), capacidad); }

    /** Re-hash sin tocar campos privados del ABB */
    private void rehash() {
        List<ArbolBinario<K, V>> vieja = tabla;
        capacidad = siguientePrimo(capacidad * 2);
        initTabla();
        for (ArbolBinario<K, V> cubeta : vieja) {
            cubeta.forEach(this::put);   // inserta cada par (k,v) en la nueva tabla
        }
    }

    private int siguientePrimo(int n) { while (!esPrimo(n)) n++; return n; }
    private boolean esPrimo(int n) {
        if (n < 2) return false;
        if (n % 2 == 0 && n != 2) return false;
        for (int i = 3; i * i <= n; i += 2) if (n % i == 0) return false;
        return true;
    }
}
