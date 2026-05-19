package luma;

import java.awt.CardLayout;
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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class Usuario implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String usuario;
	private int password;	//La contraseña se guarda en un numero cifrado
	private String nombre;
	private String archivo;
	
	public Usuario(String usuario, int password, String nombre) {
		this.usuario = usuario;
		this.password = password;
		this.nombre = nombre;
		this.archivo = usuario + "_tareas.bin";
	}
	// Get y set para el datos del usuario
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
	
	// Getters y setters
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
	private int prioridad; // 1=Alta, 2=Media, 3=Baja
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

//Manejador de archivos
class ManejadorArchivos{
	private static String ARCHIVO_USUARIOS = "usuarios.bin";
	
	//Guardar lista de usuarios
	public static void guardarUsuarios(ArrayList<Usuario> usuarios) {
		try (ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(ARCHIVO_USUARIOS))) {
			oo.writeObject(usuarios);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	//Cargar todos los usuarios
	public static ArrayList<Usuario> cargarUsuarios() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) return new ArrayList<>();
        try (ObjectInputStream oi = new ObjectInputStream(new FileInputStream(ARCHIVO_USUARIOS))) {
            return (ArrayList<Usuario>) oi.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
	
	//Buscar usuario
	public static Usuario buscarUsuario(String nombreUsuario) {
		ArrayList<Usuario> usuarios = cargarUsuarios();
		for(Usuario u : usuarios) {
			if(u.getUsuario().equals(nombreUsuario)) {
				return u;
			}
		}
		return null; //No se encontro el usuario
	}
	
	//Guardar tareas de un usuario especifico
	public static void guardarTareas(ArrayList<Tarea> tareas, String archivoUsuario) {
        try (ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(archivoUsuario))) {
            oo.writeObject(tareas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	//Cargar las tareas del usuario
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

//Este es como si fuera el main donde debe de ir todo
public class lumaScreen extends JFrame {
	
	//Con esta funcion mostraremos el Panel
	public void mostrarPanel(String nombrePanel) {
		cardLayout.show(mainPanel, nombrePanel);
	}
	private static final long serialVersionUID = 1L;
	
	////////////////////////////////////////////
	////////////////////////////////////////////
	////////////////////////////////////////////
	////////////////////////////////////////////
	////////////////////////////////////////////
	////////////////////////////////////////////
	
	// VARIABLES PARA CARDLAYOUT (Importante agregar aqui tambien los paneles y eso)
		private CardLayout cardLayout;
		private JPanel mainPanel;
		private JPanel registroPanel;
		private JPanel loginPanel;
		private JPanel appPanel;
		private JTextField textUsuario;
		private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	
	//Solo ejecuta la aplicacion
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

	/**
	 * Create the frame.
	 */
	public lumaScreen() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 610, 360);
		
		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);
		////////////////////////////////////////////
		////////////////////////////////////////////
		////////////////////////////////////////////
		////////////////////////////////////////////
		////////////////////////////////////////////
		////////////////////////////////////////////
		//Aqui agregar si se necesitan paneles nuevos
		loginPanel = new JPanel();
		registroPanel = new JPanel();
		appPanel = new JPanel();
		appPanel.add(new javax.swing.JLabel("PANEL DE LA APP"));
		registroPanel.add(new javax.swing.JLabel("PANEL DE REGISTRO"));
		
		mainPanel.add(loginPanel,"Login");
		mainPanel.add(appPanel,"App");
		mainPanel.add(registroPanel, "Registro");
		
		//Aqui empieza la creacion de objetos
		
		loginPanel.setLayout(null);
		JLabel label = new JLabel("PANEL DE LOGIN");
		label.setBounds(228, 30, 136, 35);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		loginPanel.add(label);
		
		textUsuario = new JTextField();
		textUsuario.setBounds(254, 142, 96, 19);
		loginPanel.add(textUsuario);
		textUsuario.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(254, 187, 96, 18);
		loginPanel.add(passwordField);
		
		JLabel lblNewLabel = new JLabel("Usuario:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(109, 145, 96, 12);
		loginPanel.add(lblNewLabel);
		
		JLabel lblContrasea = new JLabel("Contraseña:");
		lblContrasea.setHorizontalAlignment(SwingConstants.CENTER);
		lblContrasea.setBounds(109, 190, 96, 12);
		loginPanel.add(lblContrasea);
		
		JButton loginBtn = new JButton("Login");
		loginBtn.setBounds(254, 232, 96, 20);
		loginPanel.add(loginBtn);
		
		JButton createBtn = new JButton("Create");
		createBtn.setBounds(254, 265, 96, 20);
		loginPanel.add(createBtn);
		//Aqui termina lo de la creacion de objetos
		
		
		cardLayout.show(mainPanel,"Login");
		setContentPane(mainPanel);
		
		
		//Funciones de botones
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		
		createBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registro();
				/*ArrayList<Usuario> usuarios = ManejadorArchivos.cargarUsuarios();
				String password = "1234";
				Usuario prueba = (new Usuario("aldo1234",password.hashCode(),"aldo"));
				usuarios.add(prueba);
				ManejadorArchivos.guardarUsuarios(usuarios);
				*/
				// Esa es la forma de guardar el usuario, obvio no va aqui jaja, solo lo use para poner un usuario prueba
			}
		});

	}
	
	//Funcion de login
	public void login() {
		String usuario = textUsuario.getText().trim(); //El .trim() es una forma de correccion de error de ingreso del usuario " aldo" -> "aldo"
		String password = new String(passwordField.getPassword());
		
		if(usuario.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Llena todos los campos");
			return;
		}
		
		Usuario encontrado = ManejadorArchivos.buscarUsuario(usuario);
		if(encontrado != null && encontrado.getPassword() == password.hashCode()) {
			//Usamos hashCode porque la contraseña es un numero que se genera mediante hashCode por lo tanto se compara con este mismo
			JOptionPane.showMessageDialog(this, "¡Bienvenido " + encontrado.getNombre() + "!");
			mostrarPanel("App");
		}else {
			JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
		}
	}
	
	//Funcion de registro
	public void registro() {
		mostrarPanel("Registro");
	}
	
}
