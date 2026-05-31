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
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class Usuario implements java.io.Serializable{
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
    public int getPassword() { return password; }
    public String getNombre() { return nombre; }
    public String getArchivo() { return archivo; }
}

class Tarea implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	protected String titulo;
	protected String descripcion;
	protected boolean completada;
	protected String fechaCreacion;
	
	public Tarea(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.completada = false;
        this.fechaCreacion = java.time.LocalDate.now().toString();
    }
	
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
    public String getFechaCreacion() { return fechaCreacion; }
    
    public String getTipo() {
    	return "Tarea";
    }
    
    @Override
    public String toString() {
    	return titulo + " [" + getTipo() + "]";
    }
}

class TareaPrioridad extends Tarea{
	private int prioridad;
    private String fechaLimite;
    
    public TareaPrioridad(String titulo, String descripcion, int prioridad, String fechaLimite){
    	super(titulo, descripcion);
    	this.prioridad = prioridad;
        this.fechaLimite = fechaLimite;
    }
    
    @Override
    public String getTipo() {
        return "Tarea Prioritaria";
    }
    
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
    public String getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(String fechaLimite) { this.fechaLimite = fechaLimite; }
}

class ManejadorArchivos{
	private static String ARCHIVO_USUARIOS = "usuarios.bin";
	
	public static void guardarUsuarios(ArrayList<Usuario> usuarios) {
		try (ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(ARCHIVO_USUARIOS))) {
			oo.writeObject(usuarios);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static ArrayList<Usuario> cargarUsuarios() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) return new ArrayList<>();
        try (ObjectInputStream oi = new ObjectInputStream(new FileInputStream(ARCHIVO_USUARIOS))) {
            return (ArrayList<Usuario>) oi.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
	
	public static Usuario buscarUsuario(String nombreUsuario) {
		ArrayList<Usuario> usuarios = cargarUsuarios();
		for(Usuario u : usuarios) {
			if(u.getUsuario().equals(nombreUsuario)) {
				return u;
			}
		}
		return null;
	}
	
	public static void guardarTareas(ArrayList<Tarea> tareas, String archivoUsuario) {
        try (ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(archivoUsuario))) {
            oo.writeObject(tareas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
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

// Panel con gradiente para el fondo
class GradientPanel extends JPanel {
    private Color color1;
    private Color color2;
    
    public GradientPanel(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}

// Panel de tarjeta con bordes redondeados
class RoundedPanel extends JPanel {
    private Color borderColor;
    
    public RoundedPanel(Color borderColor) {
        this.borderColor = borderColor;
        setOpaque(false);
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

// Botón personalizado con efectos hover
class NeonButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private Color textColor;
    
    public NeonButton(String text, Color normalColor, Color hoverColor, Color textColor) {
        super(text);
        this.normalColor = normalColor;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(textColor);
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
                repaint();
            }
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
                repaint();
            }
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
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(getText(), x, y);
    }
}

public class lumaScreen extends JFrame {
	
	public void mostrarPanel(String nombrePanel) {
		cardLayout.show(mainPanel, nombrePanel);
	}
	
	private static final long serialVersionUID = 1L;
	
	// Paleta de colores futurista
	private Color bgDark = new Color(10, 10, 20);
	private Color bgMedium = new Color(15, 15, 30);
	private Color bgCard = new Color(20, 20, 40);
	private Color azulNeon = new Color(0, 200, 255);
	private Color azulOscuro = new Color(0, 100, 200);
	private Color rojoNeon = new Color(255, 50, 80);
	private Color rojoOscuro = new Color(200, 0, 50);
	private Color textoBlanco = new Color(230, 230, 240);
	private Color textoGris = new Color(160, 160, 180);
	private Color bordeSuave = new Color(60, 60, 100);
	
	private CardLayout cardLayout;
	private JPanel mainPanel;
	private JPanel loginPanel;
	private JPanel registroPanel;
	private JPanel appPanel;
	private JTextField textUsuario;
	private JPasswordField passwordField;
	
	// Variables para registro
	private JTextField regUsuario;
	private JPasswordField regPassword;
	private JTextField regNombre;
	
	// Variables para la app
	private JLabel tituloApp;
	private JLabel subtituloApp;
	private RoundedPanel panelTareas;
	private NeonButton btnAgregarTarea;
	private NeonButton btnCerrarSesion;
	
	// Dimensiones de la ventana
	private final int VENTANA_ANCHO = 1060;
	private final int VENTANA_ALTO = 660;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					lumaScreen frame = new lumaScreen();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public lumaScreen() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, VENTANA_ANCHO, VENTANA_ALTO);
		setTitle("LUMA - Task Manager");
		setResizable(false);
		setLocationRelativeTo(null);
		
		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);
		
		// ==========================================
		// PANEL DE LOGIN
		// ==========================================
		loginPanel = new GradientPanel(bgDark, bgMedium);
		loginPanel.setLayout(null);
		
		// Título principal
		JLabel tituloLogin = new JLabel("L U M A");
		tituloLogin.setBounds(0, 60, VENTANA_ANCHO, 70);
		tituloLogin.setHorizontalAlignment(SwingConstants.CENTER);
		tituloLogin.setFont(new Font("Segoe UI", Font.BOLD, 52));
		tituloLogin.setForeground(azulNeon);
		loginPanel.add(tituloLogin);
		
		// Subtítulo
		JLabel subtituloLogin = new JLabel("GESTOR DE TAREAS");
		subtituloLogin.setBounds(0, 130, VENTANA_ANCHO, 30);
		subtituloLogin.setHorizontalAlignment(SwingConstants.CENTER);
		subtituloLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		subtituloLogin.setForeground(textoGris);
		loginPanel.add(subtituloLogin);
		
		// Tarjeta de login
		RoundedPanel cardLogin = new RoundedPanel(azulNeon);
		cardLogin.setBounds(280, 190, 500, 320);
		cardLogin.setLayout(null);
		loginPanel.add(cardLogin);
		
		// Etiqueta Usuario
		JLabel lblUsuario = new JLabel("USUARIO");
		lblUsuario.setBounds(50, 35, 400, 25);
		lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblUsuario.setForeground(azulNeon);
		cardLogin.add(lblUsuario);
		
		// Campo Usuario
		textUsuario = new JTextField();
		textUsuario.setBounds(50, 65, 400, 50);
		textUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		textUsuario.setBackground(new Color(30, 30, 50));
		textUsuario.setForeground(textoBlanco);
		textUsuario.setCaretColor(azulNeon);
		textUsuario.setBorder(BorderFactory.createCompoundBorder(
		    new LineBorder(bordeSuave, 1, true),
		    new EmptyBorder(0, 15, 0, 15)
		));
		cardLogin.add(textUsuario);
		
		// Etiqueta Contraseña
		JLabel lblPassword = new JLabel("CONTRASEÑA");
		lblPassword.setBounds(50, 130, 400, 25);
		lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblPassword.setForeground(azulNeon);
		cardLogin.add(lblPassword);
		
		// Campo Contraseña
		passwordField = new JPasswordField();
		passwordField.setBounds(50, 160, 400, 50);
		passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		passwordField.setBackground(new Color(30, 30, 50));
		passwordField.setForeground(textoBlanco);
		passwordField.setCaretColor(azulNeon);
		passwordField.setBorder(BorderFactory.createCompoundBorder(
		    new LineBorder(bordeSuave, 1, true),
		    new EmptyBorder(0, 15, 0, 15)
		));
		cardLogin.add(passwordField);
		
		// Botón Login
		NeonButton loginBtn = new NeonButton("INICIAR SESIÓN", azulOscuro, azulNeon, textoBlanco);
		loginBtn.setBounds(50, 250, 400, 45);
		loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
		loginBtn.setBackground(azulOscuro);
		cardLogin.add(loginBtn);
		
		// Enlace registro
		JLabel lblRegistro = new JLabel("¿No tienes cuenta? Regístrate");
		lblRegistro.setBounds(0, 560, VENTANA_ANCHO, 30);
		lblRegistro.setHorizontalAlignment(SwingConstants.CENTER);
		lblRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblRegistro.setForeground(rojoNeon);
		lblRegistro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		loginPanel.add(lblRegistro);
		
		// ==========================================
		// PANEL DE REGISTRO
		// ==========================================
		registroPanel = new GradientPanel(bgDark, bgMedium);
		registroPanel.setLayout(null);
		
		// Título
		JLabel tituloRegistro = new JLabel("CREAR CUENTA");
		tituloRegistro.setBounds(0, 40, VENTANA_ANCHO, 60);
		tituloRegistro.setHorizontalAlignment(SwingConstants.CENTER);
		tituloRegistro.setFont(new Font("Segoe UI", Font.BOLD, 42));
		tituloRegistro.setForeground(rojoNeon);
		registroPanel.add(tituloRegistro);
		
		// Tarjeta de registro
		RoundedPanel cardRegistro = new RoundedPanel(rojoNeon);
		cardRegistro.setBounds(280, 120, 500, 400);
		cardRegistro.setLayout(null);
		registroPanel.add(cardRegistro);
		
		// Campo Nombre
		JLabel lblNombreReg = new JLabel("NOMBRE COMPLETO");
		lblNombreReg.setBounds(50, 30, 400, 25);
		lblNombreReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblNombreReg.setForeground(rojoNeon);
		cardRegistro.add(lblNombreReg);
		
		regNombre = new JTextField();
		regNombre.setBounds(50, 60, 400, 45);
		regNombre.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		regNombre.setBackground(new Color(30, 30, 50));
		regNombre.setForeground(textoBlanco);
		regNombre.setCaretColor(rojoNeon);
		regNombre.setBorder(BorderFactory.createCompoundBorder(
		    new LineBorder(bordeSuave, 1, true),
		    new EmptyBorder(0, 15, 0, 15)
		));
		cardRegistro.add(regNombre);
		
		// Campo Usuario
		JLabel lblUsuarioReg = new JLabel("NOMBRE DE USUARIO");
		lblUsuarioReg.setBounds(50, 125, 400, 25);
		lblUsuarioReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblUsuarioReg.setForeground(rojoNeon);
		cardRegistro.add(lblUsuarioReg);
		
		regUsuario = new JTextField();
		regUsuario.setBounds(50, 155, 400, 45);
		regUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		regUsuario.setBackground(new Color(30, 30, 50));
		regUsuario.setForeground(textoBlanco);
		regUsuario.setCaretColor(rojoNeon);
		regUsuario.setBorder(BorderFactory.createCompoundBorder(
		    new LineBorder(bordeSuave, 1, true),
		    new EmptyBorder(0, 15, 0, 15)
		));
		cardRegistro.add(regUsuario);
		
		// Campo Contraseña
		JLabel lblPasswordReg = new JLabel("CONTRASEÑA");
		lblPasswordReg.setBounds(50, 220, 400, 25);
		lblPasswordReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblPasswordReg.setForeground(rojoNeon);
		cardRegistro.add(lblPasswordReg);
		
		regPassword = new JPasswordField();
		regPassword.setBounds(50, 250, 400, 45);
		regPassword.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		regPassword.setBackground(new Color(30, 30, 50));
		regPassword.setForeground(textoBlanco);
		regPassword.setCaretColor(rojoNeon);
		regPassword.setBorder(BorderFactory.createCompoundBorder(
		    new LineBorder(bordeSuave, 1, true),
		    new EmptyBorder(0, 15, 0, 15)
		));
		cardRegistro.add(regPassword);
		
		// Botón Registro
		NeonButton registroBtn = new NeonButton("CREAR CUENTA", rojoOscuro, rojoNeon, textoBlanco);
		registroBtn.setBounds(50, 330, 400, 45);
		registroBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
		registroBtn.setBackground(rojoOscuro);
		cardRegistro.add(registroBtn);
		
		// Enlace volver
		JLabel lblVolverLogin = new JLabel("¿Ya tienes cuenta? Inicia sesión");
		lblVolverLogin.setBounds(0, 560, VENTANA_ANCHO, 30);
		lblVolverLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblVolverLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblVolverLogin.setForeground(azulNeon);
		lblVolverLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		registroPanel.add(lblVolverLogin);
		
		
		// PANEL DE LA APP//
		appPanel = new GradientPanel(bgDark, bgMedium);
		appPanel.setLayout(null);
		
		// Título de la app
		tituloApp = new JLabel("MIS TAREAS");
		tituloApp.setBounds(0, 40, VENTANA_ANCHO, 70);
		tituloApp.setHorizontalAlignment(SwingConstants.CENTER);
		tituloApp.setFont(new Font("Segoe UI", Font.BOLD, 48));
		tituloApp.setForeground(azulNeon);
		appPanel.add(tituloApp);
		
		// Subtítulo con nombre de usuario
		subtituloApp = new JLabel("Bienvenido de vuelta");
		subtituloApp.setBounds(0, 105, VENTANA_ANCHO, 30);
		subtituloApp.setHorizontalAlignment(SwingConstants.CENTER);
		subtituloApp.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		subtituloApp.setForeground(textoGris);
		appPanel.add(subtituloApp);
		
		// Panel principal de tareas (más grande)
		panelTareas = new RoundedPanel(azulNeon);
		panelTareas.setBounds(80, 155, 900, 294);
		panelTareas.setLayout(null);
		appPanel.add(panelTareas);
		
		// Texto de cuando no hay tareas
		JLabel lblSinTareas = new JLabel("No hay tareas pendientes");
		lblSinTareas.setBounds(0, 160, 900, 40);
		lblSinTareas.setHorizontalAlignment(SwingConstants.CENTER);
		lblSinTareas.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		lblSinTareas.setForeground(textoGris);
		panelTareas.add(lblSinTareas);
		
		// Icono de tareas (Pendiente)
		JLabel lblIconoTareas = new JLabel("&");
		lblIconoTareas.setBounds(0, 80, 900, 70);
		lblIconoTareas.setHorizontalAlignment(SwingConstants.CENTER);
		lblIconoTareas.setFont(new Font("Segoe UI", Font.PLAIN, 60));
		panelTareas.add(lblIconoTareas);
		
		// Botón para agregar tarea
		btnAgregarTarea = new NeonButton("+ NUEVA TAREA", azulOscuro, azulNeon, textoBlanco);
		btnAgregarTarea.setBounds(280, 471, 500, 50);
		btnAgregarTarea.setFont(new Font("Segoe UI", Font.BOLD, 20));
		btnAgregarTarea.setBackground(azulOscuro);
		appPanel.add(btnAgregarTarea);
		
		// Botón para cerrar sesión
		btnCerrarSesion = new NeonButton("CERRAR SESIÓN", rojoOscuro, rojoNeon, textoBlanco);
		btnCerrarSesion.setBounds(280, 559, 500, 30);
		btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnCerrarSesion.setBackground(rojoOscuro);
		appPanel.add(btnCerrarSesion);
		
		// Agregar paneles al mainPanel
		mainPanel.add(loginPanel, "Login");
		mainPanel.add(registroPanel, "Registro");
		mainPanel.add(appPanel, "App");
		
		cardLayout.show(mainPanel, "Login");
		setContentPane(mainPanel);
		
		// ==========================================
		// EVENTOS
		// ==========================================
		
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		
		lblRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				mostrarPanel("Registro");
			}
			public void mouseEntered(java.awt.event.MouseEvent e) {
				lblRegistro.setForeground(new Color(255, 100, 120));
			}
			public void mouseExited(java.awt.event.MouseEvent e) {
				lblRegistro.setForeground(rojoNeon);
			}
		});
		
		registroBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registrarUsuario();
			}
		});
		
		lblVolverLogin.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				mostrarPanel("Login");
			}
			public void mouseEntered(java.awt.event.MouseEvent e) {
				lblVolverLogin.setForeground(new Color(100, 220, 255));
			}
			public void mouseExited(java.awt.event.MouseEvent e) {
				lblVolverLogin.setForeground(azulNeon);
			}
		});

		passwordField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		
		// Evento del botón cerrar sesión
		btnCerrarSesion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirmacion = JOptionPane.showConfirmDialog(null, 
					"¿Estás seguro de que quieres cerrar sesión?", 
					"Cerrar sesión", 
					JOptionPane.YES_NO_OPTION);
				if(confirmacion == JOptionPane.YES_OPTION) {
					mostrarPanel("Login");
				}
			}
		});
		
		// Evento del botón nueva tarea
		btnAgregarTarea.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Funcionalidad de agregar tareas próximamente...");
			}
		});
	}
	
	public void login() {
		String usuario = textUsuario.getText().trim();
		String password = new String(passwordField.getPassword());
		
		if(usuario.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", 
				"Campos vacíos", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		Usuario encontrado = ManejadorArchivos.buscarUsuario(usuario);
		if(encontrado != null && encontrado.getPassword() == password.hashCode()) {
			// Actualizar el subtítulo con el nombre del usuario
			subtituloApp.setText("Bienvenido de vuelta, " + encontrado.getNombre());
			
			JOptionPane.showMessageDialog(this, "¡Bienvenido de vuelta, " + encontrado.getNombre() + "!");
			textUsuario.setText("");
			passwordField.setText("");
			mostrarPanel("App");
		} else {
			JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", 
				"Error de inicio de sesión", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void registrarUsuario() {
		String nombre = regNombre.getText().trim();
		String usuario = regUsuario.getText().trim();
		String password = new String(regPassword.getPassword());
		
		if(nombre.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", 
				"Campos vacíos", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		if(usuario.length() < 4) {
			JOptionPane.showMessageDialog(this, "El nombre de usuario debe tener al menos 4 caracteres.", 
				"Usuario inválido", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		if(password.length() < 4) {
			JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 4 caracteres.", 
				"Contraseña insegura", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		if(ManejadorArchivos.buscarUsuario(usuario) != null) {
			JOptionPane.showMessageDialog(this, "El nombre de usuario ya está en uso.", 
				"Usuario existente", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		ArrayList<Usuario> usuarios = ManejadorArchivos.cargarUsuarios();
		Usuario nuevoUsuario = new Usuario(usuario, password.hashCode(), nombre);
		usuarios.add(nuevoUsuario);
		ManejadorArchivos.guardarUsuarios(usuarios);
		
		JOptionPane.showMessageDialog(this, "¡Cuenta creada exitosamente!\nBienvenido/a " + nombre + " ", 
			"Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
		
		regNombre.setText("");
		regUsuario.setText("");
		regPassword.setText("");
		
		mostrarPanel("Login");
	}
}