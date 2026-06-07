package luma;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
//las siguientes librerias son para hacer funcionar al calendario
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;


/*Las siguientes clases heredan de algunas librerias, que son las que gestionan los usuarios y tareas
 * estas se guardan en un archivo binario (uno para cada usuario)
 * */
class Usuario implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String usuario;
    private int password;
    private String nombre;
    private String archivo;

    public Usuario(String usuario, int password, String nombre) {
        this.usuario = usuario;
        this.password = password;
        this.nombre = nombre;
        this.archivo = usuario + "_tareas.bin";
    }

    public String getUsuario() { return usuario; }
    public int getPassword()   { return password; }
    public String getNombre()  { return nombre; }
    public String getArchivo() { return archivo; }
}


class Tarea implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    protected String titulo;
    protected String descripcion;
    protected boolean completada;
    protected String fechaCreacion;
    protected String jcombo;
	protected String categoria;
    protected String fechaCompletada;
    
    public Tarea(String titulo, String descripcion, String categoria) {
        this.titulo       = titulo;
        this.descripcion  = descripcion;
        this.completada   = false;
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        this.fechaCreacion = java.time.LocalDate.now().format(formato);
        this.categoria = categoria;
    }

    public String getTitulo()       { return titulo; }
    public void   setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion()  { return descripcion; }
    public void   setDescripcion(String d) { this.descripcion = d; }
    public boolean isCompletada()   { return completada; }
    public void   setCompletada(boolean c) {
        this.completada = c;
        if (c) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            this.fechaCompletada = java.time.LocalDate.now().format(fmt);
        } else {
            this.fechaCompletada = null;
        }
    }
    public String getFechaCompletada() { return fechaCompletada; }
    public String getFechaCreacion(){ return fechaCreacion; }
    public String getCategoria()    { return categoria; }

    public String getTipo() { return "Tarea"; }
    
    public String getEstado() { return completada ? "FINALIZADA" : "PENDIENTE"; }

    @Override
    public String toString() { return titulo + " [" + getTipo() + "]"; }
}

/*Esta clase hereda las caracteristicas de la clase tarea, en esta cuando se intenta llamar a algun
atributo que posea la tarea, aplica el polimorfismo para arrojar el tipo de dato que se desea, principalmente
en la clase de tarea render*/
class TareaPrioridad extends Tarea {
    private int    prioridad;
    private String fechaLimite;

    public TareaPrioridad(String titulo, String descripcion, String categoria,
                          int prioridad, String fechaLimite) {
        super(titulo, descripcion, categoria);  
        this.prioridad   = prioridad;
        this.fechaLimite = fechaLimite;
    }

    @Override
    public String getTipo() { return "Tarea Prioritaria"; }

    public int    getPrioridad()   { return prioridad; }
    public void   setPrioridad(int p) { this.prioridad = p; }
    public String getFechaLimite() { return fechaLimite; }
    public void   setFechaLimite(String f) { this.fechaLimite = f; }

    @Override
    public String toString() {
        String cat  = (categoria  != null && !categoria.isEmpty())  ? " | Cat: " + categoria  : "";
        String fLim = (fechaLimite != null && !fechaLimite.isEmpty()) ? " | Límite: " + fechaLimite : "";
        String estado = completada ? " ✓ FINALIZADA" : "";
        return titulo + " [" + getTipo() + "]" + cat + fLim + estado;
    }
}

/*la clase manejador de archivos va a ser la encargada de gestionar todo lo del archivo
o sea, guardar tareas, cargar usuarios en un arraylist, basicamente considerarlo como 
una de las clases mas importantes, ya que con esta se va a mostrar cada tarea de cada usuario*/
class ManejadorArchivos {
    private static String ARCHIVO_USUARIOS = "usuarios.bin";

    public static void guardarUsuarios(ArrayList<Usuario> usuarios) {
        try (ObjectOutputStream oo = new ObjectOutputStream(
                new FileOutputStream(ARCHIVO_USUARIOS))) {
            oo.writeObject(usuarios);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Usuario> cargarUsuarios() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) return new ArrayList<>();
        try (ObjectInputStream oi = new ObjectInputStream(
                new FileInputStream(ARCHIVO_USUARIOS))) {
            return (ArrayList<Usuario>) oi.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static Usuario buscarUsuario(String nombreUsuario) {
        for (Usuario u : cargarUsuarios())
            if (u.getUsuario().equals(nombreUsuario)) return u;
        return null;
    }

    public static void guardarTareas(ArrayList<Tarea> tareas, String archivoUsuario) {
        try (ObjectOutputStream oo = new ObjectOutputStream(
                new FileOutputStream(archivoUsuario))) {
            oo.writeObject(tareas);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Tarea> cargarTareas(String archivoUsuario) {
        File archivo = new File(archivoUsuario);
        if (!archivo.exists()) return new ArrayList<>();
        try (ObjectInputStream oi = new ObjectInputStream(
                new FileInputStream(archivoUsuario))) {
            return (ArrayList<Tarea>) oi.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}


class GradientPanel extends JPanel {
    private Color color1, color2;
    public GradientPanel(Color color1, Color color2) {
        this.color1 = color1; this.color2 = color2; setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setPaint(new GradientPaint(0, 0, color1, 0, getHeight(), color2));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}


class RoundedPanel extends JPanel {
    private Color borderColor;
    public RoundedPanel(Color borderColor) {
        this.borderColor = borderColor; setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(20, 20, 35, 220));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2d.setColor(borderColor);
        g2d.setStroke(new java.awt.BasicStroke(1.5f));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
    }
}

class TareaRenderer extends JLabel implements ListCellRenderer<Tarea>{
    public TareaRenderer() {
        setOpaque(true);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
    
    @Override
    public Component getListCellRendererComponent(
            JList<? extends Tarea> list,
            Tarea tarea,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        
        String tiempo = "";
        String estado = tarea.isCompletada() ? "FINALIZADA" : "";
        
        if (tarea.isCompletada()) {
            //muestra la fecha en que se completo
            String fc = tarea.getFechaCompletada();
            if (fc != null && !fc.isEmpty()) {
                tiempo = "Completada el " + fc;
            } else {
                tiempo = "Completada";
            }
        } else if (tarea instanceof TareaPrioridad) {
            try{
                String fechaTexto = ((TareaPrioridad) tarea).getFechaLimite();
                java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate limite = LocalDate.parse(fechaTexto, formato);
                long dias = ChronoUnit.DAYS.between(LocalDate.now(), limite);
                tiempo = fechaTexto + " (" + dias + " días)";
            }
            catch(Exception e) {
                tiempo = "";
            }
        }
        //cambia los colores segun el estado actual de la tarea
        if (tarea.isCompletada()) {
            setBackground(new Color(30, 50, 30));
            setForeground(new Color(150, 200, 150));
        } else if (isSelected) {
            setBackground(new Color(0,100,200));
            setForeground(Color.WHITE);
        } else {
            setBackground(new Color(15,15,30));
            setForeground(Color.WHITE);
        }
        
        Color colorTiempo = Color.WHITE;
        Color colorEstado = new Color(100, 200, 100);
        
        if (tarea.isCompletada()) {
            colorTiempo = new Color(100, 150, 100);
            colorEstado = new Color(100, 255, 100);
        } else if (tarea instanceof TareaPrioridad) {
            try {
                String fechaTexto = ((TareaPrioridad)tarea).getFechaLimite();
                java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate limite = LocalDate.parse(fechaTexto, formato);
                long dias = ChronoUnit.DAYS.between(LocalDate.now(), limite);
                if (dias <= 2) {
                    colorTiempo = Color.RED;
                }
                else if (dias <= 7) {
                    colorTiempo = Color.YELLOW;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        
        String colorFecha = "#FFFFFF";
        if (colorTiempo == Color.RED) {
            colorFecha = "#FF4040";
        }
        else if (colorTiempo == Color.YELLOW) {
            colorFecha = "#FFD700";
        }
        else if (tarea.isCompletada()) {
            colorFecha = "#66AA66";
        }
        
        String colorEstadoHex = tarea.isCompletada() ? "#66FF66" : "#66AA66";
        
        setText(
            "<html><table width='100%'><tr>" +
            "<td><font color='" + (tarea.isCompletada() ? "#AACCAA" : "#FFFFFF") + "'>" +
            tarea.getTitulo() +
            "</font></td>" +
            "<td align='right'><font color='" + colorEstadoHex + "'>" +
            estado +
            "</font> <font color='" + colorFecha + "'>" +
            tiempo +
            "</font></td>" +
            "</tr></table></html>"
        );
        return this;
    }
}


class NeonButton extends JButton {
    private Color normalColor, hoverColor, textColor;

    public NeonButton(String text, Color normalColor, Color hoverColor, Color textColor) {
        super(text);
        this.normalColor = normalColor; this.hoverColor = hoverColor; this.textColor = textColor;
        setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(textColor);
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        setBackground(normalColor);
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { setBackground(hoverColor); repaint(); }
            public void mouseExited(MouseEvent e)  { setBackground(normalColor); repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground() != null ? getBackground() : normalColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2d.setFont(getFont());
        g2d.setColor(textColor);
        java.awt.FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth()  - fm.stringWidth(getText())) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(getText(), x, y);
    }
}

class PanelCalendario extends JPanel {
    private static final long serialVersionUID = 1L;
    private YearMonth mesActual;
    private ArrayList<Tarea> tareas;
    private JPanel gridDias;
    private JLabel lblMesAnio;
    private JPanel panelLeyenda;

    /*colores asignados por categoria, principalmente se usa un mapa hash
     * para relacionar el nombre de la materia/categoria con un color especifico*/
    private static final Map<String, Color> COLORES_CAT = new HashMap<>();
    static {
        COLORES_CAT.put("Matematicas",  new Color(255, 80,  80));
        COLORES_CAT.put("Redes",        new Color(80,  180, 255));
        COLORES_CAT.put("Programacion", new Color(80,  255, 180));
        COLORES_CAT.put("Ensamblador",  new Color(255, 200, 50));
        COLORES_CAT.put("UNIX",         new Color(200, 100, 255));
        COLORES_CAT.put("Fisica",       new Color(255, 140, 50));
        COLORES_CAT.put("Laboratorio",  new Color(50,  230, 230));
        COLORES_CAT.put("Español",      new Color(255, 100, 180));
        COLORES_CAT.put("Otro",         new Color(160, 160, 160));
    }

    public static Color colorCategoria(String cat) {
        if (cat == null) return COLORES_CAT.get("Otro");
        return COLORES_CAT.getOrDefault(cat, COLORES_CAT.get("Otro"));
    }

    public PanelCalendario() {
        mesActual = YearMonth.now();
        setLayout(new java.awt.BorderLayout());
        setOpaque(false);
        construir();
    }

    private void construir() {
        removeAll();

        JPanel cabecera = new JPanel(null);
        cabecera.setOpaque(false);
        cabecera.setPreferredSize(new java.awt.Dimension(0, 42));

        NeonButton btnPrev = new NeonButton("<", new Color(0,80,160), new Color(0,180,255), Color.WHITE);
        btnPrev.setBounds(30, 5, 38, 32);
        btnPrev.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cabecera.add(btnPrev);

        lblMesAnio = new JLabel("", SwingConstants.CENTER);
        lblMesAnio.setBounds(80, 5, 500, 32);
        lblMesAnio.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblMesAnio.setForeground(new Color(0, 200, 255));
        cabecera.add(lblMesAnio);

        NeonButton btnNext = new NeonButton(">", new Color(0,80,160), new Color(0,180,255), Color.WHITE);
        btnNext.setBounds(590, 5, 38, 32);
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cabecera.add(btnNext);

        btnPrev.addActionListener(e -> { mesActual = mesActual.minusMonths(1); actualizar(); });
        btnNext.addActionListener(e -> { mesActual = mesActual.plusMonths(1); actualizar(); });

        JPanel cuerpo = new JPanel(new java.awt.BorderLayout(8, 0));
        cuerpo.setOpaque(false);

        JPanel contenedorGrid = new JPanel(new java.awt.BorderLayout());
        contenedorGrid.setOpaque(false);

        String[] dias = {"Lun","Mar","Mié","Jue","Vie","Sáb","Dom"};
        JPanel headerDias = new JPanel(new java.awt.GridLayout(1, 7, 4, 0));
        headerDias.setOpaque(false);
        for (String d : dias) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(new Color(160, 160, 200));
            headerDias.add(lbl);
        }
        contenedorGrid.add(headerDias, java.awt.BorderLayout.NORTH);

        gridDias = new JPanel(new java.awt.GridLayout(0, 7, 4, 4));
        gridDias.setOpaque(false);
        contenedorGrid.add(gridDias, java.awt.BorderLayout.CENTER);

        //panel de la derecha donde se muestran las tareas, con su respectivo color
        panelLeyenda = new JPanel();
        panelLeyenda.setLayout(new javax.swing.BoxLayout(panelLeyenda, javax.swing.BoxLayout.Y_AXIS));
        panelLeyenda.setOpaque(false);
        panelLeyenda.setPreferredSize(new java.awt.Dimension(185, 0));
        panelLeyenda.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60,60,100), 1, true),
            new EmptyBorder(10, 10, 10, 10)));

        JLabel lblLeyTit = new JLabel("TAREAS DEL MES");
        lblLeyTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLeyTit.setForeground(new Color(0, 200, 255));
        lblLeyTit.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelLeyenda.add(lblLeyTit);
        panelLeyenda.add(javax.swing.Box.createVerticalStrut(8));

        cuerpo.add(contenedorGrid, java.awt.BorderLayout.CENTER);
        cuerpo.add(panelLeyenda,   java.awt.BorderLayout.EAST);

        add(cabecera, java.awt.BorderLayout.NORTH);
        add(cuerpo,   java.awt.BorderLayout.CENTER);

        actualizar();
    }

    public void setTareas(ArrayList<Tarea> tareas) {
        this.tareas = tareas;
        actualizar();
    }

    private void actualizar() {
        java.time.format.DateTimeFormatter fmtMes =
            java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", new java.util.Locale("es","MX"));
        lblMesAnio.setText(mesActual.format(fmtMes).toUpperCase());

        /*En esta seccion utiliza un mapa hash utilizando como llave el dia que se le asingo de entrega a la tarea,
         en base a eso, puede acomodarlas dentro del calendario*/
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Map<LocalDate, ArrayList<Tarea>> mapa = new HashMap<>();
        if (tareas != null) {
            for (Tarea t : tareas) {
            	if(t.isCompletada()) {
            		continue;
            	}
                if (t instanceof TareaPrioridad) {
                    String fl = ((TareaPrioridad) t).getFechaLimite();
                    if (fl != null && !fl.isEmpty()) {
                        try {
                            LocalDate fecha = LocalDate.parse(fl, fmt);
                            mapa.computeIfAbsent(fecha, k -> new ArrayList<>()).add(t);
                        } catch (Exception ignored) {}
                    }
                }
            }
        }

        gridDias.removeAll();
        LocalDate primeroDiaMes = mesActual.atDay(1);
        int diaSemanaInicio = primeroDiaMes.getDayOfWeek().getValue(); //el 1 representa el lunes
        for (int i = 1; i < diaSemanaInicio; i++) gridDias.add(new JLabel(""));

        LocalDate hoy = LocalDate.now();
        int totalDias = mesActual.lengthOfMonth();
        for (int d = 1; d <= totalDias; d++) {
            LocalDate fecha = mesActual.atDay(d);
            ArrayList<Tarea> tareasDelDia = mapa.getOrDefault(fecha, new ArrayList<>());
            JPanel celda = crearCelda(d, fecha.equals(hoy), tareasDelDia);
            gridDias.add(celda);
        }
        gridDias.revalidate();
        gridDias.repaint();

        panelLeyenda.removeAll();
        JLabel lblLeyTit = new JLabel("TAREAS DEL MES");
        lblLeyTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLeyTit.setForeground(new Color(0, 200, 255));
        lblLeyTit.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelLeyenda.add(lblLeyTit);
        panelLeyenda.add(javax.swing.Box.createVerticalStrut(8));

        //hace una lista de todas las tareas del mes
        for (Map.Entry<LocalDate, ArrayList<Tarea>> e : mapa.entrySet()) {
            if (e.getKey().getYear() == mesActual.getYear() &&
                e.getKey().getMonthValue() == mesActual.getMonthValue()) {
                for (Tarea t : e.getValue()) {
                    panelLeyenda.add(crearFilaLeyenda(t));
                    panelLeyenda.add(javax.swing.Box.createVerticalStrut(5));
                }
            }
        }
        if (panelLeyenda.getComponentCount() <= 2) {
            JLabel vacio = new JLabel("Sin tareas este mes");
            vacio.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            vacio.setForeground(new Color(120,120,150));
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelLeyenda.add(vacio);
        }
        panelLeyenda.revalidate();
        panelLeyenda.repaint();
    }

    private JPanel crearFilaLeyenda(Tarea t) {
        JPanel fila = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 22));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel punto = new JLabel("●");
        punto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        punto.setForeground(PanelCalendario.colorCategoria(t.getCategoria()));
        fila.add(punto);

        String titulo = t.getTitulo().length() > 16 ? t.getTitulo().substring(0,14)+"…" : t.getTitulo();
        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(t.isCompletada() ? new Color(100,200,100) : new Color(220,220,240));
        fila.add(lblTit);
        return fila;
    }

    private JPanel crearCelda(int dia, boolean esHoy, ArrayList<Tarea> tareasDelDia) {
        JPanel celda = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fondo = esHoy ? new Color(0, 60, 100) : new Color(20, 20, 38);
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                if (esHoy) {
                    g2.setColor(new Color(0, 200, 255));
                    g2.setStroke(new java.awt.BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                }
            }
        };
        celda.setLayout(new java.awt.BorderLayout(0, 1));
        celda.setOpaque(false);

        JLabel numDia = new JLabel(String.valueOf(dia), SwingConstants.CENTER);
        numDia.setFont(new Font("Segoe UI", esHoy ? Font.BOLD : Font.PLAIN, 12));
        numDia.setForeground(esHoy ? new Color(0,220,255) : new Color(200,200,220));
        celda.add(numDia, java.awt.BorderLayout.NORTH);

        //esta parte marca unas franjas de colores para distinguir una tarea de la otra, si es que hay varias en un solo dia
        if (!tareasDelDia.isEmpty()) {
            JPanel franjas = new JPanel(new java.awt.GridLayout(
                Math.min(tareasDelDia.size(), 3), 1, 0, 1));
            franjas.setOpaque(false);
            int mostrar = Math.min(tareasDelDia.size(), 3);
            for (int i = 0; i < mostrar; i++) {
                Tarea t = tareasDelDia.get(i);
                Color c = PanelCalendario.colorCategoria(t.getCategoria());
                JPanel franja = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        g.setColor(c);
                        g.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                    }
                };
                franja.setOpaque(false);
                franja.setPreferredSize(new java.awt.Dimension(0, 5));
                franja.setToolTipText(t.getTitulo() + " [" + t.getCategoria() + "]");
                franjas.add(franja);
            }
            celda.add(franjas, java.awt.BorderLayout.SOUTH);
        }
        return celda;
    }
}

//─── Panel Estadísticas ─────────────────────────────────────────────────────
class PanelEstadisticas extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<Tarea> tareas;
	private JPanel contenido;
	
	public PanelEstadisticas() {
	     setLayout(new java.awt.BorderLayout());
	     setOpaque(false);
	     contenido = new JPanel();
	     contenido.setLayout(new java.awt.GridLayout(2, 3, 14, 14));
	     contenido.setOpaque(false);
	     contenido.setBorder(new EmptyBorder(10, 10, 10, 10));
	     add(contenido, java.awt.BorderLayout.CENTER);
	}
	
	public void setTareas(ArrayList<Tarea> tareas) {
	     this.tareas = tareas;
	     actualizar();
	}
	
	private void actualizar() {
	     contenido.removeAll();
	     if (tareas == null) { contenido.revalidate(); return; }
	
	     int total       = tareas.size();
	     int completadas = (int) tareas.stream().filter(Tarea::isCompletada).count();
	     int pendientes  = total - completadas;
	
	     // Contar por categoría
	     Map<String, Long> porCat = new java.util.TreeMap<>();
	     for (Tarea t : tareas) {
	         String cat = t.getCategoria() != null ? t.getCategoria() : "Otro";
	         porCat.merge(cat, 1L, Long::sum);
	     }
	     String catMayor = porCat.entrySet().stream()
	         .max(Map.Entry.comparingByValue())
	         .map(Map.Entry::getKey).orElse("—");
	
	     // Tareas vencidas (fechaLimite < hoy y no completadas)
	     LocalDate hoy = LocalDate.now();
	     java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
	     long vencidas = tareas.stream().filter(t -> !t.isCompletada() && t instanceof TareaPrioridad)
	         .filter(t -> {
	             try {
	                 String fl = ((TareaPrioridad) t).getFechaLimite();
	                 return fl != null && !fl.isEmpty() && LocalDate.parse(fl, fmt).isBefore(hoy);
	             } catch (Exception e) { return false; }
	         }).count();
	
	     double pct = total == 0 ? 0 : (completadas * 100.0 / total);
	
	     contenido.add(tarjetaStat("TOTAL TAREAS",    String.valueOf(total),      new Color(0,180,255)));
	     contenido.add(tarjetaStat("COMPLETADAS",      String.valueOf(completadas), new Color(0,220,120)));
	     contenido.add(tarjetaStat("PENDIENTES",       String.valueOf(pendientes),  new Color(255,200,50)));
	     contenido.add(tarjetaStat("VENCIDAS",         String.valueOf(vencidas),    new Color(255,70,70)));
	     contenido.add(tarjetaStat("RENDIMIENTO",      String.format("%.1f%%", pct),new Color(180,100,255)));
	     contenido.add(tarjetaStat("CATEGORIA TOP",    catMayor,                   new Color(255,140,50)));
	
	     contenido.revalidate();
	     contenido.repaint();
	}
	
	private JPanel tarjetaStat(String titulo, String valor, Color acento) {
	     JPanel card = new JPanel(null) {
	         @Override
	         protected void paintComponent(Graphics g) {
	             Graphics2D g2 = (Graphics2D) g;
	             g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	             g2.setColor(new Color(18, 18, 35));
	             g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
	             g2.setColor(acento);
	             g2.setStroke(new java.awt.BasicStroke(1.5f));
	             g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
	             // barra inferior decorativa
	             g2.setColor(acento);
	             g2.fillRoundRect(16, getHeight()-6, getWidth()-32, 4, 4, 4);
	         }
	     };
	     card.setOpaque(false);
	
	     JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
	     lblValor.setFont(new Font("Segoe UI", Font.BOLD, 34));
	     lblValor.setForeground(acento);
	     lblValor.setBounds(0, 60, 999, 44);
	     card.add(lblValor);
	
	     JLabel lblTit = new JLabel(titulo, SwingConstants.CENTER);
	     lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
	     lblTit.setForeground(new Color(160,160,200));
	     lblTit.setBounds(0, 106, 999, 22);
	     card.add(lblTit);
	
	     // Override para que los labels ocupen todo el ancho
	     card.addComponentListener(new java.awt.event.ComponentAdapter() {
	         @Override public void componentResized(java.awt.event.ComponentEvent e) {
	             int w = card.getWidth();
	             lblValor.setBounds(0, 60, w, 44);
	             lblTit.setBounds(0, 106, w, 22);
	         }
	     });
	     return card;
	}
}

//─── Panel Reporte ──────────────────────────────────────────────────────────
class PanelReporte extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<Tarea> tareas;
	private Usuario usuario;
	private javax.swing.JTextArea areaPreview;
	private JLabel lblEstado;
	
	private Color azulNeon  = new Color(0, 200, 255);
	private Color verdeNeon = new Color(0, 220, 120);
	private Color bgDark    = new Color(10, 10, 20);
	private Color textoBlanco = new Color(230, 230, 240);
	private Color textoGris   = new Color(160, 160, 180);
	private Color bordeSuave  = new Color(60, 60, 100);

	 public PanelReporte() {
	     setLayout(new java.awt.BorderLayout(0, 10));
	     setOpaque(false);
	     construir();
	 }
	
	 private void construir() {
	     // ── Título ──
	     JLabel titulo = new JLabel("GENERAR REPORTE", SwingConstants.CENTER);
	     titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
	     titulo.setForeground(azulNeon);
	     titulo.setBorder(new EmptyBorder(8, 0, 4, 0));
	     add(titulo, java.awt.BorderLayout.NORTH);
	
	     // ── Preview ──
	     areaPreview = new javax.swing.JTextArea();
	     areaPreview.setEditable(false);
	     areaPreview.setFont(new Font("Consolas", Font.PLAIN, 13));
	     areaPreview.setBackground(new Color(14, 14, 28));
	     areaPreview.setForeground(textoBlanco);
	     areaPreview.setCaretColor(azulNeon);
	     areaPreview.setBorder(new EmptyBorder(10, 14, 10, 14));
	     areaPreview.setText("  Presiona «PREVISUALIZAR» para ver el reporte antes de exportar.");
	
	     JScrollPane scrollPreview = new JScrollPane(areaPreview);
	     scrollPreview.setBorder(new LineBorder(bordeSuave, 1, true));
	     scrollPreview.getViewport().setBackground(new Color(14, 14, 28));
	     add(scrollPreview, java.awt.BorderLayout.CENTER);
	
	     // ── Botones ──
	     JPanel barraBot = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 18, 6));
	     barraBot.setOpaque(false);
	
	     NeonButton btnPreview = new NeonButton(" PREVISUALIZAR ", new Color(0,80,160), azulNeon, textoBlanco);
	     btnPreview.setFont(new Font("Segoe UI", Font.BOLD, 14));
	     btnPreview.setPreferredSize(new java.awt.Dimension(200, 42));
	     btnPreview.addActionListener(e -> previsualizarReporte());
	     barraBot.add(btnPreview);
	
	     NeonButton btnExportar = new NeonButton(" EXPORTAR .TXT ", new Color(0,100,60), verdeNeon, textoBlanco);
	     btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 14));
	     btnExportar.setPreferredSize(new java.awt.Dimension(200, 42));
	     btnExportar.addActionListener(e -> exportarReporte());
	     barraBot.add(btnExportar);
	
	     lblEstado = new JLabel("", SwingConstants.CENTER);
	     lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 13));
	     lblEstado.setForeground(verdeNeon);
	
	     JPanel sur = new JPanel(new java.awt.BorderLayout());
	     sur.setOpaque(false);
	     sur.add(barraBot, java.awt.BorderLayout.NORTH);
	     sur.add(lblEstado, java.awt.BorderLayout.SOUTH);
	     add(sur, java.awt.BorderLayout.SOUTH);
	 }
	
	 public void setDatos(ArrayList<Tarea> tareas, Usuario usuario) {
	     this.tareas  = tareas;
	     this.usuario = usuario;
	     areaPreview.setText("  Presiona «PREVISUALIZAR» para ver el reporte antes de exportar.");
	     lblEstado.setText("");
	 }
	
	 private String generarTextoReporte() {
	     if (tareas == null) return "(Sin datos de tareas)";
	     StringBuilder sb = new StringBuilder();
	     String sep = "═".repeat(60);
	     String sep2 = "─".repeat(60);
	
	     sb.append(sep).append("\n");
	     sb.append("  REPORTE LUMA - GESTOR DE TAREAS\n");
	     if (usuario != null) sb.append("  Usuario : ").append(usuario.getNombre())
	                             .append(" (@").append(usuario.getUsuario()).append(")\n");
	     sb.append("  Fecha   : ").append(LocalDate.now()
	         .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
	     sb.append(sep).append("\n\n");
	
	     long completadas = tareas.stream().filter(Tarea::isCompletada).count();
	     long pendientes  = tareas.size() - completadas;
	     sb.append("  RESUMEN GENERAL\n").append(sep2).append("\n");
	     sb.append(String.format("  Total de tareas  : %d%n", tareas.size()));
	     sb.append(String.format("  Completadas      : %d%n", completadas));
	     sb.append(String.format("  Pendientes       : %d%n", pendientes));
	     double pct = tareas.isEmpty() ? 0 : completadas * 100.0 / tareas.size();
	     sb.append(String.format("  Rendimiento      : %.1f%%%n%n", pct));
	
	     sb.append(" TAREAS COMPLETADAS\n").append(sep2).append("\n");
	     tareas.stream().filter(Tarea::isCompletada).forEach(t -> {
	         sb.append(String.format("  * %s%n", t.getTitulo()));
	         sb.append(String.format("      Categoría  : %s%n", t.getCategoria()));
	         sb.append(String.format("      Creada     : %s%n", t.getFechaCreacion()));
	         if (t.getFechaCompletada() != null)
	             sb.append(String.format("      Completada : %s%n", t.getFechaCompletada()));
	         sb.append("\n");
	     });
	     if (completadas == 0) sb.append("  Ninguna tarea completada \n\n");
	
	     sb.append("   TAREAS PENDIENTES\n").append(sep2).append("\n");
	     tareas.stream().filter(t -> !t.isCompletada()).forEach(t -> {
	         sb.append(String.format("  * %s%n", t.getTitulo()));
	         sb.append(String.format("      Categoría  : %s%n", t.getCategoria()));
	         sb.append(String.format("      Creada     : %s%n", t.getFechaCreacion()));
	         if (t instanceof TareaPrioridad) {
	             String fl = ((TareaPrioridad) t).getFechaLimite();
	             if (fl != null && !fl.isEmpty()) {
	                 sb.append(String.format("      Fecha lím. : %s%n", fl));
	                 try {
	                     java.time.format.DateTimeFormatter fmt =
	                         java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");
	                     long dias = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(fl, fmt));
	                     sb.append(String.format("      Días rest. : %d%n", dias));
	                 } catch (Exception ignored) {}
	             }
	         }
	         sb.append("\n");
	     });
	     if (pendientes == 0) sb.append("  Todas las tareas han sido completadas \n\n");
	
	     sb.append(sep).append("\n");
	     sb.append("  Reporte generado por LUMA Task Manager\n");
	     sb.append(sep).append("\n");
	     return sb.toString();
	 }
	
	 private void previsualizarReporte() {
	     areaPreview.setText(generarTextoReporte());
	     areaPreview.setCaretPosition(0);
	     lblEstado.setText("");
	 }
	
	 private void exportarReporte() {
	     String texto = generarTextoReporte();
	     String nombreArchivo = "reporte_luma_" + usuario.getUsuario() + "_" + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy")) + ".txt";
	     try (java.io.PrintWriter pw = new java.io.PrintWriter(
	         new java.io.FileWriter(nombreArchivo, java.nio.charset.StandardCharsets.UTF_8))) {
	         pw.print(texto);
	         lblEstado.setForeground(new Color(0, 220, 120));
	         lblEstado.setText("Reporte guardado como: " + nombreArchivo);
	         areaPreview.setText(texto);
	         areaPreview.setCaretPosition(0);
	     } catch (Exception ex) {
	         lblEstado.setForeground(new Color(255, 70, 70));
	         lblEstado.setText("Error al guardar: " + ex.getMessage());
	     }
	 }
}

public class lumaScreen extends JFrame {

    private static final long serialVersionUID = 1L;

    private Color bgDark     = new Color(10, 10, 20);
    private Color bgMedium   = new Color(15, 15, 30);
    private Color azulNeon   = new Color(0, 200, 255);
    private Color azulOscuro = new Color(0, 100, 200);
    private Color rojoNeon   = new Color(255, 50, 80);
    private Color rojoOscuro = new Color(200, 0, 50);
    private Color textoBlanco = new Color(230, 230, 240);
    private Color textoGris  = new Color(160, 160, 180);
    private Color bordeSuave = new Color(60, 60, 100);
    private Color verdeNeon  = new Color(0, 220, 120);
    private Color verdeOscuro = new Color(0, 120, 60);

    //layouts y paneles, tambien se definen variables para los elementos de la pantalla
    private CardLayout cardLayout;
    private JPanel mainPanel, loginPanel, registroPanel, appPanel;

    private JTextField    textUsuario;
    private JPasswordField passwordField;

    //registro
    private JTextField     regUsuario, regNombre;
    private JPasswordField regPassword;

    //app (el que agrega las tareas)
    private JLabel             tituloApp, subtituloApp;
    private RoundedPanel       panelTareas;
    private NeonButton         btnAgregarTarea, btnCerrarSesion;
    private JList<Tarea>       listaTareas;
    private DefaultListModel<Tarea> modeloTareas;
    
    //calendario 
    private CardLayout vistasLayout;
    private JPanel     vistasPanel;
    private PanelCalendario   panelCalendario;
    
    //estadisticas
    private PanelEstadisticas panelEstadisticas;
    //reporte
    private PanelReporte      panelReporte;

    //filtros
    private JComboBox<String> comboCategoria;
    private JTextField        txtFiltroFecha;
    private NeonButton        btnFiltrar, btnLimpiarFiltro,btnEliminarTarea;

    //tamaño de pantalla
    private final int VENTANA_ANCHO = 1060;
    private final int VENTANA_ALTO  = 700;

   
    private Usuario         actual;
    private ArrayList<Tarea> tareas;

    //categorias de tareas
    private static final String[] CATEGORIAS = {
        "Todas", "Matematicas", "Redes", "Programacion", "Ensamblador", "UNIX", "Fisica", "Laboratorio", "Español", "Finalizadas" };

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                lumaScreen frame = new lumaScreen();
                frame.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public lumaScreen() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, VENTANA_ANCHO, VENTANA_ALTO);
        setTitle("LUMA - Task Manager");
        setResizable(false);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);

        construirPanelLogin();
        construirPanelRegistro();
        construirPanelApp();

        mainPanel.add(loginPanel,    "Login");
        mainPanel.add(registroPanel, "Registro");
        mainPanel.add(appPanel,      "App");

        cardLayout.show(mainPanel, "Login");
        setContentPane(mainPanel);
        addWindowListener(new java.awt.event.WindowAdapter() {
        	@Override
        	public void windowClosing(java.awt.event.WindowEvent e) {
        		if(actual != null &&
        	    tareas != null) {
        			ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
        	    }
        	}
        });
    }

    private void construirPanelLogin() {
        loginPanel = new GradientPanel(bgDark, bgMedium);
        loginPanel.setLayout(null);

        JLabel tituloLogin = new JLabel("L U M A");
        tituloLogin.setBounds(0, 60, VENTANA_ANCHO, 70);
        tituloLogin.setHorizontalAlignment(SwingConstants.CENTER);
        tituloLogin.setFont(new Font("Segoe UI", Font.BOLD, 52));
        tituloLogin.setForeground(azulNeon);
        loginPanel.add(tituloLogin);

        JLabel subtituloLogin = new JLabel("GESTOR DE TAREAS");
        subtituloLogin.setBounds(0, 130, VENTANA_ANCHO, 30);
        subtituloLogin.setHorizontalAlignment(SwingConstants.CENTER);
        subtituloLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtituloLogin.setForeground(textoGris);
        loginPanel.add(subtituloLogin);

        RoundedPanel cardLogin = new RoundedPanel(azulNeon);
        cardLogin.setBounds(280, 190, 500, 320);
        cardLogin.setLayout(null);
        loginPanel.add(cardLogin);

        JLabel lblUsuario = new JLabel("USUARIO");
        lblUsuario.setBounds(50, 35, 400, 25);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuario.setForeground(azulNeon);
        cardLogin.add(lblUsuario);

        textUsuario = new JTextField();
        textUsuario.setBounds(50, 65, 400, 50);
        estilizarCampo(textUsuario, azulNeon);
        cardLogin.add(textUsuario);

        JLabel lblPassword = new JLabel("CONTRASEÑA");
        lblPassword.setBounds(50, 130, 400, 25);
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPassword.setForeground(azulNeon);
        cardLogin.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 160, 400, 50);
        estilizarCampo(passwordField, azulNeon);
        cardLogin.add(passwordField);

        NeonButton loginBtn = new NeonButton("INICIAR SESIÓN", azulOscuro, azulNeon, textoBlanco);
        loginBtn.setBounds(50, 250, 400, 45);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardLogin.add(loginBtn);

        JLabel lblRegistro = new JLabel("¿No tienes cuenta? Registrate");
        lblRegistro.setBounds(0, 560, VENTANA_ANCHO, 30);
        lblRegistro.setHorizontalAlignment(SwingConstants.CENTER);
        lblRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblRegistro.setForeground(rojoNeon);
        lblRegistro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        loginPanel.add(lblRegistro);

        loginBtn.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
        lblRegistro.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { mostrarPanel("Registro"); }
            public void mouseEntered(MouseEvent e) { lblRegistro.setForeground(new Color(255, 100, 120)); }
            public void mouseExited(MouseEvent e)  { lblRegistro.setForeground(rojoNeon); }
        });
    }

    private void construirPanelRegistro() {
        registroPanel = new GradientPanel(bgDark, bgMedium);
        registroPanel.setLayout(null);

        JLabel tituloRegistro = new JLabel("CREAR CUENTA");
        tituloRegistro.setBounds(0, 40, VENTANA_ANCHO, 60);
        tituloRegistro.setHorizontalAlignment(SwingConstants.CENTER);
        tituloRegistro.setFont(new Font("Segoe UI", Font.BOLD, 42));
        tituloRegistro.setForeground(rojoNeon);
        registroPanel.add(tituloRegistro);

        RoundedPanel cardRegistro = new RoundedPanel(rojoNeon);
        cardRegistro.setBounds(280, 120, 500, 400);
        cardRegistro.setLayout(null);
        registroPanel.add(cardRegistro);

        JLabel lblNombreReg = new JLabel("NOMBRE COMPLETO");
        lblNombreReg.setBounds(50, 30, 400, 25);
        lblNombreReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombreReg.setForeground(rojoNeon);
        cardRegistro.add(lblNombreReg);

        regNombre = new JTextField();
        regNombre.setBounds(50, 60, 400, 45);
        estilizarCampo(regNombre, rojoNeon);
        cardRegistro.add(regNombre);

        JLabel lblUsuarioReg = new JLabel("NOMBRE DE USUARIO");
        lblUsuarioReg.setBounds(50, 125, 400, 25);
        lblUsuarioReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuarioReg.setForeground(rojoNeon);
        cardRegistro.add(lblUsuarioReg);

        regUsuario = new JTextField();
        regUsuario.setBounds(50, 155, 400, 45);
        estilizarCampo(regUsuario, rojoNeon);
        cardRegistro.add(regUsuario);

        JLabel lblPasswordReg = new JLabel("CONTRASEÑA");
        lblPasswordReg.setBounds(50, 220, 400, 25);
        lblPasswordReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPasswordReg.setForeground(rojoNeon);
        cardRegistro.add(lblPasswordReg);

        regPassword = new JPasswordField();
        regPassword.setBounds(50, 250, 400, 45);
        estilizarCampo(regPassword, rojoNeon);
        cardRegistro.add(regPassword);

        NeonButton registroBtn = new NeonButton("CREAR CUENTA", rojoOscuro, rojoNeon, textoBlanco);
        registroBtn.setBounds(50, 330, 400, 45);
        registroBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cardRegistro.add(registroBtn);

        JLabel lblVolverLogin = new JLabel("¿Ya tienes cuenta? Inicia sesión");
        lblVolverLogin.setBounds(0, 560, VENTANA_ANCHO, 30);
        lblVolverLogin.setHorizontalAlignment(SwingConstants.CENTER);
        lblVolverLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblVolverLogin.setForeground(azulNeon);
        lblVolverLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        registroPanel.add(lblVolverLogin);

       
        registroBtn.addActionListener(e -> registrarUsuario());
        lblVolverLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { mostrarPanel("Login"); }
            public void mouseEntered(MouseEvent e) { lblVolverLogin.setForeground(new Color(100, 220, 255)); }
            public void mouseExited(MouseEvent e)  { lblVolverLogin.setForeground(azulNeon); }
        });
    }

    private void construirPanelApp() {
        appPanel = new GradientPanel(bgDark, bgMedium);
        appPanel.setLayout(null);

        tituloApp = new JLabel("MIS TAREAS");
        tituloApp.setBounds(0, 12, VENTANA_ANCHO, 44);
        tituloApp.setHorizontalAlignment(SwingConstants.CENTER);
        tituloApp.setFont(new Font("Segoe UI", Font.BOLD, 36));
        tituloApp.setForeground(azulNeon);
        appPanel.add(tituloApp);

        subtituloApp = new JLabel("Bienvenido de vuelta");
        subtituloApp.setBounds(0, 56, VENTANA_ANCHO, 24);
        subtituloApp.setHorizontalAlignment(SwingConstants.CENTER);
        subtituloApp.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtituloApp.setForeground(textoGris);
        appPanel.add(subtituloApp);

        //en esta seccion se muestran las opciones de navegación para las 4 ventanas
        String[] tabs = {" TAREAS ", " CALENDARIO ", " ESTADÍSTICAS ", " REPORTE "};
        Color[] tabColores = {azulNeon, new Color(255,180,50), new Color(180,100,255), verdeNeon};
        Color[] tabOscuros = {azulOscuro, new Color(140,90,0), new Color(80,0,160), verdeOscuro};
        NeonButton[] btnTabs = new NeonButton[4];
        JPanel navBar = new JPanel(new java.awt.GridLayout(1, 4, 6, 0));
        navBar.setOpaque(false);
        navBar.setBounds(40, 86, 980, 34);
        for (int i = 0; i < tabs.length; i++) {
            btnTabs[i] = new NeonButton(tabs[i], tabOscuros[i], tabColores[i], textoBlanco);
            btnTabs[i].setFont(new Font("Segoe UI", Font.BOLD, 12));
            navBar.add(btnTabs[i]);
        }
        appPanel.add(navBar);

        //panel de vistas
        vistasLayout = new CardLayout();
        vistasPanel  = new JPanel(vistasLayout);
        vistasPanel.setOpaque(false);
        vistasPanel.setBounds(40, 126, 980, 508);
        appPanel.add(vistasPanel);

        //vista de las tareas
        JPanel vistaTareas = new JPanel(null);
        vistaTareas.setOpaque(false);
        
        //vista del calendario
        panelCalendario = new PanelCalendario();
        panelCalendario.setBorder(new EmptyBorder(6, 6, 6, 6));
        
        //estadisticas
        panelEstadisticas = new PanelEstadisticas();
        panelEstadisticas.setBorder(new EmptyBorder(10, 10, 10, 10));

        //reporte
        panelReporte = new PanelReporte();
        panelReporte.setBorder(new EmptyBorder(8, 10, 8, 10));
		//esta parte es la que visualmente muestra el contenido de cada panel
        vistasPanel.add(vistaTareas,       "Tareas");
        vistasPanel.add(panelCalendario,   "Calendario");
        vistasPanel.add(panelEstadisticas, "Estadisticas");
        vistasPanel.add(panelReporte,      "Reporte");
        vistasLayout.show(vistasPanel, "Tareas");

        //panel de filtros
        RoundedPanel panelFiltros = new RoundedPanel(verdeNeon);
        panelFiltros.setBounds(0, 0, 980, 60);
        panelFiltros.setLayout(null);
        vistaTareas.add(panelFiltros);

        JLabel lblCat = new JLabel("Categoría:");
        lblCat.setBounds(15, 17, 90, 25);
        lblCat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCat.setForeground(verdeNeon);
        panelFiltros.add(lblCat);

        comboCategoria = new JComboBox<>(CATEGORIAS);
        comboCategoria.setBounds(110, 15, 160, 30);
        comboCategoria.setBackground(new Color(20, 30, 40));
        comboCategoria.setForeground(textoBlanco);
        comboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelFiltros.add(comboCategoria);

        JLabel lblFecha = new JLabel("Fecha límite:");
        lblFecha.setBounds(290, 17, 105, 25);
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFecha.setForeground(verdeNeon);
        panelFiltros.add(lblFecha);

        txtFiltroFecha = new JTextField();
        txtFiltroFecha.setBounds(400, 15, 130, 30);
        txtFiltroFecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtFiltroFecha.setBackground(new Color(20, 30, 40));
        txtFiltroFecha.setForeground(textoBlanco);
        txtFiltroFecha.setCaretColor(verdeNeon);
        txtFiltroFecha.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bordeSuave, 1, true), new EmptyBorder(0, 8, 0, 8)));
        txtFiltroFecha.setToolTipText("Formato: dd-MM-yyyy");
        panelFiltros.add(txtFiltroFecha);

        btnFiltrar = new NeonButton("FILTRAR", verdeOscuro, verdeNeon, textoBlanco);
        btnFiltrar.setBounds(545, 12, 120, 36);
        btnFiltrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFiltros.add(btnFiltrar);

        btnLimpiarFiltro = new NeonButton("LIMPIAR", new Color(50,50,80), textoGris, textoBlanco);
        btnLimpiarFiltro.setBounds(675, 12, 120, 36);
        btnLimpiarFiltro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFiltros.add(btnLimpiarFiltro);
        
        btnEliminarTarea = new NeonButton("ELIMINAR", rojoOscuro, rojoNeon, textoBlanco);
        btnEliminarTarea.setBounds(800, 12, 90, 36);
        btnEliminarTarea.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelFiltros.add(btnEliminarTarea);
        
        NeonButton btnModificarTarea = new NeonButton("MODIFICAR", azulOscuro, azulNeon, textoBlanco);
        btnModificarTarea.setBounds(900, 12, 75, 36);
        btnModificarTarea.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelFiltros.add(btnModificarTarea);
        
        btnModificarTarea.addActionListener(e -> modificarTarea());

        panelTareas = new RoundedPanel(azulNeon);
        panelTareas.setBounds(0, 68, 980, 340);
        panelTareas.setLayout(null);
        vistaTareas.add(panelTareas);

        modeloTareas = new DefaultListModel<>();
        listaTareas  = new JList<>(modeloTareas);
        listaTareas.setCellRenderer(new TareaRenderer());
        listaTareas.setBackground(new Color(15, 15, 30));
        listaTareas.setForeground(textoBlanco);
        listaTareas.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        listaTareas.setSelectionBackground(azulOscuro);
        listaTareas.setFixedCellHeight(36);

        JScrollPane scroll = new JScrollPane(listaTareas);
        scroll.setBounds(20, 20, 940, 300);
        scroll.getViewport().setBackground(new Color(15, 15, 30));
        scroll.setBorder(BorderFactory.createLineBorder(bordeSuave, 1));
        panelTareas.add(scroll);

        btnAgregarTarea = new NeonButton("+ NUEVA TAREA", azulOscuro, azulNeon, textoBlanco);
        btnAgregarTarea.setBounds(240, 420, 500, 45);
        btnAgregarTarea.setFont(new Font("Segoe UI", Font.BOLD, 19));
        vistaTareas.add(btnAgregarTarea);

        btnCerrarSesion = new NeonButton("CERRAR SESIÓN", rojoOscuro, rojoNeon, textoBlanco);
        btnCerrarSesion.setBounds(340, 472, 300, 35);
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        vistaTareas.add(btnCerrarSesion);
       
        //clics de los botones de cada panel
        btnTabs[0].addActionListener(e -> vistasLayout.show(vistasPanel, "Tareas"));
        btnTabs[1].addActionListener(e -> {
            panelCalendario.setTareas(tareas);
            vistasLayout.show(vistasPanel, "Calendario");
        });
        
        btnTabs[2].addActionListener(e -> {
            panelEstadisticas.setTareas(tareas);
            vistasLayout.show(vistasPanel, "Estadisticas");
        });
        btnTabs[3].addActionListener(e -> {
            panelReporte.setDatos(tareas, actual);
            vistasLayout.show(vistasPanel, "Reporte");
        });
        listaTareas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Tarea seleccionada = listaTareas.getSelectedValue();
                    if (seleccionada != null) mostrarDetallesTarea(seleccionada);
                }
            }
        });

        btnAgregarTarea.addActionListener(e -> agregarTarea());

        btnCerrarSesion.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(null,
                "¿Estás seguro de que quieres cerrar sesión?",
                "Cerrar sesión", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                if(actual != null && tareas != null) {
                    ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
                }
                actual = null;
                tareas = null;
                mostrarPanel("Login");
            }
        });

        btnFiltrar.addActionListener(e -> aplicarFiltros());

        btnLimpiarFiltro.addActionListener(e -> {
            comboCategoria.setSelectedIndex(0);
            txtFiltroFecha.setText("");
            actualizarListaTareas();
        });
        
        btnEliminarTarea.addActionListener(e -> eliminarTarea());
    }

    private void estilizarCampo(javax.swing.text.JTextComponent campo, Color acent) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        campo.setBackground(new Color(30, 30, 50));
        campo.setForeground(textoBlanco);
        campo.setCaretColor(acent);
        campo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bordeSuave, 1, true), new EmptyBorder(0, 15, 0, 15)));
    }
    
    //muestra los detalles de cada tarea al ahcer clic sobre una 
    private void mostrarDetallesTarea(Tarea tarea) {
        String estado = tarea.isCompletada() ? "FINALIZADA" : "PENDIENTE";
        String mensaje = "Estado: " + estado + "\n\nTítulo: " + tarea.getTitulo() +
                		 "\n\nDescripción:\n" + tarea.getDescripcion() +
                		 "\n\nCategoría: " + tarea.getCategoria();
        if (tarea instanceof TareaPrioridad) {
            mensaje += "\n\nFecha límite: " + ((TareaPrioridad)tarea).getFechaLimite();
        }
        //cambia las opciones segun el estado
        Object[] opciones;
        if (tarea.isCompletada()) {
            opciones = new Object[] { "Reabrir", "Modificar", "Cerrar" };
        } else {
            opciones = new Object[] { "Finalizar", "Modificar", "Cerrar" };
        }
        int opcion = JOptionPane.showOptionDialog(
                this,
                mensaje,
                "Detalles de tarea",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[2]);
        if (opcion == 0) {
            if (tarea.isCompletada()) {
                //reabrir tarea
                tarea.setCompletada(false);
                ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
                actualizarListaTareas();
                JOptionPane.showMessageDialog(this, "Tarea reabierta");
            } else {
                //finalizar tarea
                tarea.setCompletada(true);
                ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
                actualizarListaTareas();
                JOptionPane.showMessageDialog(this, "¡Tarea finalizada!");
            }
        } else if (opcion == 1) {
            //modificar tarea
            modificarTareaSeleccionada(tarea);
        }
    }

    private void modificarTareaSeleccionada(Tarea seleccionada) {
        String nuevoTitulo = JOptionPane.showInputDialog(this, "Nuevo nombre de la tarea:", seleccionada.getTitulo());
        if (nuevoTitulo == null || nuevoTitulo.trim().isEmpty()) return;
        String nuevaDescripcion = JOptionPane.showInputDialog(this, "Nueva descripción:", seleccionada.getDescripcion());
        if (nuevaDescripcion == null) nuevaDescripcion = seleccionada.getDescripcion();
        String[] categorias = {"Tareas finalizadas","Matematicas", "Redes", "Programacion", "Ensamblador", 
                              "UNIX", "Fisica", "Laboratorio", "Español"};
        String nuevaCategoria = (String) JOptionPane.showInputDialog(this, "Selecciona una categoría:", "Modificar Categoría",
        															 JOptionPane.PLAIN_MESSAGE, null,
        															 categorias, seleccionada.getCategoria());
        if (nuevaCategoria == null) nuevaCategoria = seleccionada.getCategoria();
        String nuevaFechaLimite = "";
        if (seleccionada instanceof TareaPrioridad) {
            TareaPrioridad tp = (TareaPrioridad) seleccionada;
            nuevaFechaLimite = JOptionPane.showInputDialog(this,
                "Nueva fecha limite (dd-MM-yyyy):", tp.getFechaLimite());
            if (nuevaFechaLimite == null) nuevaFechaLimite = tp.getFechaLimite();
        }
        seleccionada.setTitulo(nuevoTitulo.trim());
        seleccionada.setDescripcion(nuevaDescripcion.trim());
        seleccionada.categoria = nuevaCategoria;
        if (seleccionada instanceof TareaPrioridad && !nuevaFechaLimite.isEmpty()) {
            ((TareaPrioridad) seleccionada).setFechaLimite(nuevaFechaLimite.trim());
        }
        
        ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
        actualizarListaTareas();
        
        JOptionPane.showMessageDialog(this, 
            "Tarea modificada exitosamente", "Modificación exitosa", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    //mostrar panel
    public void mostrarPanel(String nombrePanel) {
        cardLayout.show(mainPanel, nombrePanel);
    }

    /*todo el metodo de login valida que el usuario y contraseña sean correctos a los introducidos
    incluso, valida que los campos hayan sido rellenados, una vez completado el login, pasa al siguiente frame,
    el cual, es todo el gestor de tareas como tal*/
    public void login() {
        String usuario  = textUsuario.getText().trim();
        String password = new String(passwordField.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, completa todos los campos.", "Campos vacíos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario encontrado = ManejadorArchivos.buscarUsuario(usuario);
        if (encontrado != null && encontrado.getPassword() == password.hashCode()) {
            actual = encontrado;
            tareas = ManejadorArchivos.cargarTareas(actual.getArchivo());
            subtituloApp.setText("Bienvenido de vuelta, " + encontrado.getNombre());
            JOptionPane.showMessageDialog(this,
                "¡Bienvenido de vuelta, " + encontrado.getNombre() + "!");
            textUsuario.setText("");
            passwordField.setText("");
            actualizarListaTareas();
            mostrarPanel("App");
        } else {
            JOptionPane.showMessageDialog(this,
                "Usuario o contraseña incorrectos.", "Error de inicio de sesión",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    //actualiza las tareas que existan en el archivo (solo muestra pendientes)
    public void actualizarListaTareas() {
        modeloTareas.clear();
        if (tareas == null) return;
        for (Tarea t : tareas) {
            if (!t.isCompletada()) modeloTareas.addElement(t);
        }
    }

    //filtrar por categoria
    public void aplicarFiltros() {
        if (tareas == null) return;
        String catSeleccionada = (String) comboCategoria.getSelectedItem();
        String fechaBuscada    = txtFiltroFecha.getText().trim();
        modeloTareas.clear();
        boolean filtrarFinalizadas = "Finalizadas".equals(catSeleccionada);
        for (Tarea t : tareas) {
            if (filtrarFinalizadas) {
                //muestra tareas completadas
                if (t.isCompletada()) modeloTareas.addElement(t);
                continue;
            }
            //con los demas filtros no muestra las completadas
            if (t.isCompletada()) continue;
            boolean pasaCat   = catSeleccionada == null
                             || catSeleccionada.equals("Todas")
                             || catSeleccionada.equalsIgnoreCase(t.getCategoria());
            boolean pasaFecha = true;
            if (!fechaBuscada.isEmpty() && t instanceof TareaPrioridad) {
                String fl = ((TareaPrioridad) t).getFechaLimite();
                pasaFecha = fl != null && fl.equals(fechaBuscada);
            } else if (!fechaBuscada.isEmpty() && !(t instanceof TareaPrioridad)) {
                pasaFecha = false;
            }
            if (pasaCat && pasaFecha) modeloTareas.addElement(t);
        }
        
        if (modeloTareas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No se encontraron tareas.", "Sin resultados",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void eliminarTarea(){
        int indice = listaTareas.getSelectedIndex();
        if (indice == -1){
            JOptionPane.showMessageDialog(this,"Seleccione una tarea");
            return;
        }
        Tarea seleccionada = listaTareas.getSelectedValue();
        int opc = JOptionPane.showConfirmDialog(this,
            "¿Desea eliminar la tarea?" + seleccionada.getTitulo(), "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION
        );
        if (opc == JOptionPane.YES_OPTION) {
            tareas.remove(seleccionada);
            ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
            actualizarListaTareas();
            JOptionPane.showMessageDialog(this,"Tarea eliminada");
        }
    }

    public void agregarTarea() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la tarea:");
        if (nombre == null || nombre.trim().isEmpty()) return;

        String descripcion = JOptionPane.showInputDialog(this, "Descripción:");
        if (descripcion == null) descripcion = "";

        //este joptionPane contiene las cateogrias de tareas
        String categoria = (String) JOptionPane.showInputDialog(
            this, "Selecciona una categoría:", "Categoría",
            JOptionPane.PLAIN_MESSAGE, null,
            new String[]{"Matematicas", "Redes", "Programacion", "Ensamblador", "UNIX", "Fisica", "Laboratorio", "Español" }, "Categoría");
        if (categoria == null) categoria = "Otro";

        String fechaLimite = JOptionPane.showInputDialog(this,
            "Fecha límite (dd-MM-yyyy). Deja vacío si no aplica:");
        if (fechaLimite == null) fechaLimite = "";

        TareaPrioridad nueva = new TareaPrioridad(
            nombre.trim(), descripcion.trim(), categoria, 1, fechaLimite.trim());
        tareas.add(nueva);
        ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
        actualizarListaTareas();

        JOptionPane.showMessageDialog(this,
            "Tarea \"" + nombre + "\" agregada correctamente.", "Tarea creada",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void modificarTarea() {
        int indice = listaTareas.getSelectedIndex();
        if (indice == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una tarea para modificar");
            return;
        }
        Tarea seleccionada = listaTareas.getSelectedValue();
        //cambiar titulo
        String nuevoTitulo = JOptionPane.showInputDialog(this, 
            "Nuevo nombre de la tarea:", seleccionada.getTitulo());
        if (nuevoTitulo == null) return;
        if (nuevoTitulo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío");
            return;
        }
        
        //cambiar descripcion
        String nuevaDescripcion = JOptionPane.showInputDialog(this, 
            "Nueva descripción:", seleccionada.getDescripcion());
        if (nuevaDescripcion == null) nuevaDescripcion = seleccionada.getDescripcion();
        
        //categoria
        String[] categorias = {"Matematicas", "Redes", "Programacion", "Ensamblador", 
                              "UNIX", "Fisica", "Laboratorio", "Español"};
        String nuevaCategoria = (String) JOptionPane.showInputDialog(
            this, "Selecciona una categoría:", "Modificar Categoría",
            JOptionPane.PLAIN_MESSAGE, null,
            categorias, seleccionada.getCategoria());
        if (nuevaCategoria == null) nuevaCategoria = seleccionada.getCategoria();
        
        //modificar fecha limite
        String nuevaFechaLimite = "";
        if (seleccionada instanceof TareaPrioridad) {
        	TareaPrioridad tp = (TareaPrioridad) seleccionada;
            nuevaFechaLimite = JOptionPane.showInputDialog(this, "Nueva fecha limite (dd-MM-yyyy):", tp.getFechaLimite());
            if (nuevaFechaLimite == null) nuevaFechaLimite = tp.getFechaLimite();
        }
        
        seleccionada.setTitulo(nuevoTitulo.trim());
        seleccionada.setDescripcion(nuevaDescripcion.trim());
        seleccionada.categoria = nuevaCategoria;
        
        if (seleccionada instanceof TareaPrioridad && !nuevaFechaLimite.isEmpty()) {
            ((TareaPrioridad) seleccionada).setFechaLimite(nuevaFechaLimite.trim());
        }
        
        //guardar cambios
        ManejadorArchivos.guardarTareas(tareas, actual.getArchivo());
        actualizarListaTareas();
        
        JOptionPane.showMessageDialog(this, 
            "Tarea modificada exitosamente", "Modificación exitosa", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void registrarUsuario() {
        String nombre   = regNombre.getText().trim();
        String usuario  = regUsuario.getText().trim();
        String password = new String(regPassword.getPassword());

        if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.",
                "Campos vacíos", JOptionPane.WARNING_MESSAGE); return;
        }
        if (usuario.length() < 4) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario debe tener al menos 4 caracteres.",
                "Usuario inválido", JOptionPane.WARNING_MESSAGE); return;
        }
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 4 caracteres.",
                "Contraseña insegura", JOptionPane.WARNING_MESSAGE); return;
        }
        if (ManejadorArchivos.buscarUsuario(usuario) != null) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario ya está en uso.",
                "Usuario existente", JOptionPane.WARNING_MESSAGE); return;
        }

        ArrayList<Usuario> usuarios = ManejadorArchivos.cargarUsuarios();
        usuarios.add(new Usuario(usuario, password.hashCode(), nombre));
        ManejadorArchivos.guardarUsuarios(usuarios);

        JOptionPane.showMessageDialog(this,
            "¡Cuenta creada exitosamente!\nBienvenido/a " + nombre,
            "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);

        regNombre.setText(""); regUsuario.setText(""); regPassword.setText("");
        mostrarPanel("Login");
    }
}