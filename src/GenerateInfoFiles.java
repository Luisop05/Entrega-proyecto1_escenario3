import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase GenerateInfoFiles para generar archivos planos pseudoaleatorios
 * que servirán como entrada para el programa principal de análisis de ventas.
 * 
 * Esta clase genera tres tipos de archivos:
 * 1. Archivos de información de vendedores
 * 2. Archivos de información de productos
 * 3. Archivos individuales de ventas por vendedor
 */
public class GenerateInfoFiles {
    
    // Listas de nombres y apellidos reales para generar datos coherentes
    private static final String[] NOMBRES = {
        "Carlos", "María", "José", "Ana", "Luis", "Carmen", "Miguel", "Rosa",
        "Juan", "Isabel", "Antonio", "Pilar", "Manuel", "Mercedes", "Francisco",
        "Dolores", "Jesús", "Josefa", "Javier", "Teresa", "Daniel", "Concepción",
        "Rafael", "Francisca", "David", "Antonia", "Pedro", "Esperanza", "Alejandro",
        "Cristina"
    };
    
    private static final String[] APELLIDOS = {
        "García", "González", "Rodríguez", "Fernández", "López", "Martínez",
        "Sánchez", "Pérez", "Gómez", "Martín", "Jiménez", "Ruiz", "Hernández",
        "Díaz", "Moreno", "Muñoz", "Álvarez", "Romero", "Alonso", "Gutiérrez",
        "Navarro", "Torres", "Domínguez", "Vázquez", "Ramos", "Gil", "Ramírez",
        "Serrano", "Blanco", "Suárez"
    };
    
    private static final String[] PRODUCTOS = {
        "Laptop", "Mouse", "Teclado", "Monitor", "Impresora", "Cámara Web",
        "Auriculares", "Tablet", "Smartphone", "Cargador", "Disco Duro",
        "Memoria USB", "Router", "Parlantes", "Micrófono", "Proyector",
        "Scanner", "Webcam", "Mousepad", "Cable HDMI", "Adaptador", "Batería",
        "Funda", "Soporte", "Hub USB"
    };
    
    private static final String[] TIPOS_DOCUMENTO = {"CC", "CE", "TI", "PP"};
    
    private static Random random = new Random();
    private static List<VendedorInfo> vendedoresGenerados = new ArrayList<>();
    
    /**
     * Clase interna para mantener la información completa del vendedor
     */
    private static class VendedorInfo {
        String tipoDocumento;
        long numeroDocumento;
        String nombres;
        String apellidos;
        
        public VendedorInfo(String tipoDoc, long numDoc, String nombres, String apellidos) {
            this.tipoDocumento = tipoDoc;
            this.numeroDocumento = numDoc;
            this.nombres = nombres;
            this.apellidos = apellidos;
        }
        
        public String getIdentificacion() {
            return tipoDocumento + ";" + numeroDocumento;
        }
    }
    
    /**
     * Método principal que ejecuta la generación de todos los archivos necesarios.
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        try {
            System.out.println("=== INICIANDO GENERACIÓN DE ARCHIVOS ===");
            
            // Generar archivo de información de productos
            createProductsFile(25);
            System.out.println("✓ Archivo de productos generado exitosamente");
            
            // Generar archivo de información de vendedores
            createSalesManInfoFile(10);
            System.out.println("✓ Archivo de información de vendedores generado exitosamente");
            
            // Generar archivos de ventas individuales para cada vendedor
            generateSalesFiles();
            System.out.println("✓ Archivos de ventas por vendedor generados exitosamente");
            
            System.out.println("=== GENERACIÓN COMPLETADA EXITOSAMENTE ===");
            System.out.println("Archivos generados:");
            System.out.println("- productos.txt");
            System.out.println("- vendedores.txt");
            System.out.println("- " + vendedoresGenerados.size() + " archivos de ventas individuales");
            
        } catch (Exception e) {
            System.err.println("ERROR: Ocurrió un problema durante la generación de archivos:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crea un archivo con información pseudoaleatoria de productos.
     * 
     * @param productsCount cantidad de productos a generar
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createProductsFile(int productsCount) throws IOException {
        if (productsCount <= 0) {
            throw new IllegalArgumentException("La cantidad de productos debe ser mayor a 0");
        }
        
        FileWriter writer = new FileWriter("productos.txt");
        
        try {
            for (int i = 1; i <= productsCount; i++) {
                String idProducto = String.format("PROD%03d", i);
                String nombreProducto = PRODUCTOS[random.nextInt(PRODUCTOS.length)] + " " + 
                                       generarModeloAleatorio();
                double precio = generarPrecioAleatorio();
                
                writer.write(idProducto + ";" + nombreProducto + ";" + 
                           String.format("%.2f", precio) + "\n");
            }
        } finally {
            writer.close();
        }
    }
    
    /**
     * Crea un archivo con información de vendedores generada pseudoaleatoriamente.
     * 
     * @param salesmanCount cantidad de vendedores a generar
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        if (salesmanCount <= 0) {
            throw new IllegalArgumentException("La cantidad de vendedores debe ser mayor a 0");
        }
        
        FileWriter writer = new FileWriter("vendedores.txt");
        vendedoresGenerados.clear();
        
        try {
            for (int i = 0; i < salesmanCount; i++) {
                String tipoDocumento = TIPOS_DOCUMENTO[random.nextInt(TIPOS_DOCUMENTO.length)];
                long numeroDocumento = generarNumeroDocumento();
                String nombres = generarNombres();
                String apellidos = generarApellidos();
                
                // Crear objeto vendedor y agregarlo a la lista
                VendedorInfo vendedor = new VendedorInfo(tipoDocumento, numeroDocumento, nombres, apellidos);
                vendedoresGenerados.add(vendedor);
                
                // Escribir al archivo
                String lineaVendedor = tipoDocumento + ";" + numeroDocumento + ";" + 
                                     nombres + ";" + apellidos;
                writer.write(lineaVendedor + "\n");
            }
        } finally {
            writer.close();
        }
    }
    
    /**
     * Crea un archivo pseudoaleatorio de ventas para un vendedor específico.
     * 
     * @param randomSalesCount cantidad aleatoria de ventas a generar
     * @param name nombre del vendedor (no usado en esta implementación)
     * @param id identificador del vendedor
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) 
            throws IOException {
        if (randomSalesCount <= 0) {
            throw new IllegalArgumentException("La cantidad de ventas debe ser mayor a 0");
        }
        
        // Buscar el vendedor en la lista para obtener su tipo de documento
        VendedorInfo vendedorEncontrado = null;
        for (VendedorInfo v : vendedoresGenerados) {
            if (v.numeroDocumento == id) {
                vendedorEncontrado = v;
                break;
            }
        }
        
        if (vendedorEncontrado == null) {
            throw new IllegalArgumentException("No se encontró el vendedor con ID: " + id);
        }
        
        String nombreArchivo = "ventas_" + vendedorEncontrado.tipoDocumento + "_" + id + ".txt";
        FileWriter writer = new FileWriter(nombreArchivo);
        
        try {
            // Primera línea: información del vendedor (debe coincidir con vendedores.txt)
            writer.write(vendedorEncontrado.getIdentificacion() + "\n");
            
            // Generar ventas aleatorias
            for (int i = 0; i < randomSalesCount; i++) {
                String idProducto = String.format("PROD%03d", random.nextInt(25) + 1);
                int cantidad = random.nextInt(20) + 1; // Entre 1 y 20 unidades
                
                writer.write(idProducto + ";" + cantidad + ";\n");
            }
        } finally {
            writer.close();
        }
    }
    
    /**
     * Genera archivos de ventas para todos los vendedores creados.
     */
    private static void generateSalesFiles() {
        try {
            for (VendedorInfo vendedor : vendedoresGenerados) {
                int cantidadVentas = random.nextInt(15) + 5; // Entre 5 y 19 ventas
                createSalesMenFile(cantidadVentas, "", vendedor.numeroDocumento);
            }
        } catch (IOException e) {
            System.err.println("Error generando archivos de ventas: " + e.getMessage());
        }
    }
    
    /**
     * Genera un número de documento aleatorio.
     * 
     * @return número de documento entre 10000000 y 99999999
     */
    private static long generarNumeroDocumento() {
        return random.nextInt(90000000) + 10000000L;
    }
    
    /**
     * Genera uno o dos nombres aleatorios de la lista predefinida.
     * 
     * @return cadena con uno o dos nombres separados por espacio
     */
    private static String generarNombres() {
        String primerNombre = NOMBRES[random.nextInt(NOMBRES.length)];
        
        // 30% de probabilidad de tener segundo nombre
        if (random.nextDouble() < 0.3) {
            String segundoNombre = NOMBRES[random.nextInt(NOMBRES.length)];
            return primerNombre + " " + segundoNombre;
        }
        
        return primerNombre;
    }
    
    /**
     * Genera uno o dos apellidos aleatorios de la lista predefinida.
     * 
     * @return cadena con uno o dos apellidos separados por espacio
     */
    private static String generarApellidos() {
        String primerApellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
        String segundoApellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
        
        return primerApellido + " " + segundoApellido;
    }
    
    /**
     * Genera un modelo aleatorio para complementar el nombre de productos.
     * 
     * @return cadena que representa un modelo (ej: "Pro", "Max", "v2.0")
     */
    private static String generarModeloAleatorio() {
        String[] modelos = {"Pro", "Max", "Plus", "Lite", "Standard", "Premium", 
                           "v2.0", "v3.0", "2024", "2025", "Ultra", "Mini"};
        return modelos[random.nextInt(modelos.length)];
    }
    
    /**
     * Genera un precio aleatorio para productos.
     * 
     * @return precio entre 50.00 y 5000.00
     */
    private static double generarPrecioAleatorio() {
        return random.nextDouble() * 4950.0 + 50.0; // Entre 50 y 5000
    }
}