package algoritmosdispersion;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.function.Function;

/**
 * Ventana principal que permite comparar dos métodos de dispersión:
 *  1) Direccionamiento abierto con prueba lineal (tabla {@link Lineal})
 *  2) Encadenamiento con un ABB por cubeta (tabla {@link EncadenamientoAB})
 *
 * En cada pestaña el usuario puede:
 *  • Insertar un cliente (si escribe DNI + nombre + correo) 
 *  • Buscar un cliente (si escribe solo el DNI)
 *  • Ver el resultado justo a la derecha del botón "Insertar / Buscar".
 *  • Observar en la parte inferior los tiempos medidos (micro‑segundos).
 */
public class UI extends JFrame {

    /* ---------- estructuras de dispersión ---------- */
    private final Lineal<String, Cliente>          lineal      = new Lineal<>();
    private final EncadenamientoAB<String,Cliente> encadenado  = new EncadenamientoAB<>();

    /* ---------- área de log ---------- */
    private final JTextArea log = new JTextArea(10, 45);

    public UI() {
        super("Algoritmos de Dispersión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /* ===== pestañas ===== */
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Lineal",          crearPanel(this::insertarLineal,      this::buscarLineal,      "L"));
        tabs.addTab("Encadenado ABB",  crearPanel(this::insertarEncadenado,  this::buscarEncadenado,  "E"));

        /* ===== área de log ===== */
        log.setEditable(false);
        JScrollPane scroll = new JScrollPane(log);

        add(tabs, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    /* ===================================================================== */
    /*                       MÉTODOS DE INSERCIÓN / BÚSQUEDA                 */
    /* ===================================================================== */
    private void insertarLineal(String dni, Cliente c){ lineal.put(dni, c); }
    private Cliente buscarLineal(String dni){ return lineal.get(dni); }

    private void insertarEncadenado(String dni, Cliente c){ encadenado.put(dni, c); }
    private Cliente buscarEncadenado(String dni){ return encadenado.get(dni); }

    /* ===================================================================== */
    /*                       CONSTRUCCIÓN DE PANEL                           */
    /* ===================================================================== */
    private JPanel crearPanel(java.util.function.BiConsumer<String,Cliente> insertar,
                              Function<String,Cliente> buscar,
                              String etiquetaPrefijo)
    {
        /* ----- contenedor principal ----- */
        JPanel root = new JPanel(new BorderLayout(5,5));

        /* ----- rejilla con los 3 campos ----- */
        JPanel datos = new JPanel(new GridLayout(3,2,5,5));
        JLabel lblDni = new JLabel("DNI:");
        JLabel lblNom = new JLabel("Nombre:");
        JLabel lblCor = new JLabel("Correo:");
        JTextField txtDni = new JTextField();
        JTextField txtNom = new JTextField();
        JTextField txtCor = new JTextField();

        datos.add(lblDni); datos.add(txtDni);
        datos.add(lblNom); datos.add(txtNom);
        datos.add(lblCor); datos.add(txtCor);

        /* ----- zona inferior con botón + resultado ----- */
        JButton btn = new JButton("Insertar / Buscar");
        JLabel  lblRes = new JLabel();
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        fila.add(btn);
        fila.add(lblRes);

        root.add(datos, BorderLayout.CENTER);
        root.add(fila,  BorderLayout.SOUTH);

        /* ---------- acción del botón ---------- */
        btn.addActionListener(e -> {
            String dni = txtDni.getText().trim();
            String nom = txtNom.getText().trim();
            String cor = txtCor.getText().trim();
            if(dni.isEmpty()) return;              // DNI obligatorio

            /* inserción sólo si se especifican nombre y correo */
            if(!nom.isEmpty() && !cor.isEmpty()){
                medirVoid(() -> insertar.accept(dni, new Cliente(dni, nom, cor)),
                          "INSERT-"+etiquetaPrefijo);
            }

            /* búsqueda */
            Cliente cli = medir(() -> buscar.apply(dni), "GET-"+etiquetaPrefijo);

            if(cli!=null){
                lblRes.setText(cli.getNombre()+" ("+cli.getDni()+")");
                txtNom.setText(cli.getNombre());
                txtCor.setText(cli.getCorreo());
            }else{
                lblRes.setText("No encontrado");
                txtNom.setText("");
                txtCor.setText("");
            }
        });

        return root;
    }

    /* ===================================================================== */
    /*                       MÉTODOS DE MEDICIÓN                             */
    /* ===================================================================== */
    /** mide una operación que devuelve resultado */
    private <T> T medir(java.util.function.Supplier<T> tarea, String etiqueta){
        long ini = System.nanoTime();
        T res = tarea.get();
        long fin = System.nanoTime();
        log.append(String.format("%s: %d µs%n", etiqueta, (fin-ini)/1_000));
        return res;
    }

    /** mide una operación void */
    private void medirVoid(Runnable tarea, String etiqueta){
        long ini = System.nanoTime();
        tarea.run();
        long fin = System.nanoTime();
        log.append(String.format("%s: %d µs%n", etiqueta, (fin-ini)/1_000));
    }

}
