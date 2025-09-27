import java.io.*;
import java.util.*;

/**
 * Clase main para procesar archivos de ventas y generar reportes.
 * 
 * Esta clase lee los archivos generados por GenerateInfoFiles y produce:
 * 1. Reporte de vendedores ordenados por dinero recaudado (descendente)
 * 2. Reporte de productos vendidos ordenados por cantidad (descendente)
 * 
 * @author Equipo Escenario 3
 * @version 1.0
 */
public class main {
    
    // Estructuras para almacenar la información
    private static Map<String, Vendedor> vendedores = new HashMap<>();
    private static Map<String, Producto> productos = new HashMap<>();
    private static Map<String, Integer> ventasPorProducto = new HashMap<>();
    private static Map<String, Double> recaudacionPorVendedor = new HashMap<>();
    
    /**
     * Clase interna para representar un vendedor
     */
    private static class Vendedor {
        String tipoDocumento;
        String numeroDocumento;
        String nombres;
        String apellidos;
        
        public Vendedor(String tipoDoc, String numDoc, String nombres, String apellidos) {
            this.tipoDocumento = tipoDoc;
            this.numeroDocumento = numDoc;
            this.nombres = nombres;
            this.apellidos = apellidos;
        }
        
        public String getNombreCompleto() {
            return nombres + " " + apellidos;
        }
        
        public String getIdentificacion() {
            return tipoDocumento + ";" + numeroDocumento;
        }
    }
    
    /**
     * Clase interna para representar un producto
     */
    private static class Producto {
        String id;
        String nombre;
        double precio;
        
        public Producto(String id, String nombre, double precio) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
        }
    }
    
    /**
     * Método principal que ejecuta el procesamiento de archivos y generación de reportes.
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        try {
            System.out.println("=== INICIANDO PROCESAMIENTO DE ARCHIVOS DE VENTAS ===");
            
            // Cargar información de vendedores
            cargarVendedores();
            System.out.println("✓ Información de vendedores cargada exitosamente");
            
            // Cargar información de productos
            cargarProductos();
            System.out.println("✓ Información de productos cargada exitosamente");
            
            // Procesar archivos de ventas
            procesarArchivosDeVentas();
            System.out.println("✓ Archivos de ventas procesados exitosamente");
            
            // Generar reporte de vendedores
            generarReporteVendedores();
            System.out.println("✓ Reporte de vendedores generado: reporte_vendedores.csv");
            
            // Generar reporte de productos
            generarReporteProductos();
            System.out.println("✓ Reporte de productos generado: reporte_productos.csv");
            
            System.out.println("=== PROCESAMIENTO COMPLETADO EXITOSAMENTE ===");
            System.out.println("Archivos generados:");
            System.out.println("- reporte_vendedores.csv");
            System.out.println("- reporte_productos.csv");
            
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: No se encontró uno de los archivos necesarios:");
            System.err.println("Verifique que existan: vendedores.txt, productos.txt y los archivos de ventas");
            System.err.println("Detalle: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("ERROR: Problema al leer o escribir archivos:");
            System.err.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Formato numérico inválido en los archivos:");
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: Ocurrió un problema inesperado:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga la información de vendedores desde el archivo vendedores.txt
     * 
     * @throws IOException si hay problemas al leer el archivo
     */
    private static void cargarVendedores() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("vendedores.txt"));
        String linea;
        
        while ((linea = reader.readLine()) != null) {
            if (linea.trim().isEmpty()) continue;
            
            String[] partes = linea.split(";");
            if (partes.length >= 4) {
                String tipoDoc = partes[0].trim();
                String numDoc = partes[1].trim();
                String nombres = partes[2].trim();
                String apellidos = partes[3].trim();
                
                String clave = tipoDoc + ";" + numDoc;
                vendedores.put(clave, new Vendedor(tipoDoc, numDoc, nombres, apellidos));
                recaudacionPorVendedor.put(clave, 0.0);
            } else {
                System.out.println("ADVERTENCIA: Línea con formato incorrecto en vendedores.txt: " + linea);
            }
        }
        reader.close();
        
        if (vendedores.isEmpty()) {
            throw new IOException("No se encontraron vendedores válidos en vendedores.txt");
        }
    }
    
    /**
     * Carga la información de productos desde el archivo productos.txt
     * 
     * @throws IOException si hay problemas al leer el archivo
     */
    private static void cargarProductos() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("productos.txt"));
        String linea;
        
        while ((linea = reader.readLine()) != null) {
            if (linea.trim().isEmpty()) continue;
            
            String[] partes = linea.split(";");
            if (partes.length >= 3) {
                String id = partes[0].trim();
                String nombre = partes[1].trim();
                double precio;
                
                try {
                    precio = Double.parseDouble(partes[2].trim().replace(",", "."));
                } catch (NumberFormatException e) {
                    System.out.println("ADVERTENCIA: Precio inválido para producto " + id + ": " + partes[2]);
                    continue;
                }
                
                productos.put(id, new Producto(id, nombre, precio));
                ventasPorProducto.put(id, 0);
            } else {
                System.out.println("ADVERTENCIA: Línea con formato incorrecto en productos.txt: " + linea);
            }
        }
        reader.close();
        
        if (productos.isEmpty()) {
            throw new IOException("No se encontraron productos válidos en productos.txt");
        }
    }
    
    /**
     * Procesa todos los archivos de ventas encontrados en el directorio actual
     * 
     * @throws IOException si hay problemas al leer los archivos
     */
    private static void procesarArchivosDeVentas() throws IOException {
        File directorio = new File(".");
        File[] archivos = directorio.listFiles((dir, nombre) -> 
            nombre.startsWith("ventas_") && nombre.endsWith(".txt"));
        
        if (archivos == null || archivos.length == 0) {
            throw new IOException("No se encontraron archivos de ventas en el directorio actual");
        }
        
        int archivosValidos = 0;
        for (File archivo : archivos) {
            try {
                if (procesarArchivoVentas(archivo)) {
                    archivosValidos++;
                }
            } catch (Exception e) {
                System.out.println("ADVERTENCIA: Error procesando " + archivo.getName() + ": " + e.getMessage());
            }
        }
        
        if (archivosValidos == 0) {
            throw new IOException("No se pudo procesar ningún archivo de ventas válido");
        }
        
        System.out.println("Archivos de ventas procesados: " + archivosValidos + "/" + archivos.length);
    }
    
    /**
     * Procesa un archivo individual de ventas
     * 
     * @param archivo el archivo a procesar
     * @return true si el archivo se procesó correctamente
     * @throws IOException si hay problemas al leer el archivo
     */
    private static boolean procesarArchivoVentas(File archivo) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        String primeraLinea = reader.readLine();
        
        if (primeraLinea == null || primeraLinea.trim().isEmpty()) {
            reader.close();
            throw new IOException("Archivo vacío: " + archivo.getName());
        }
        
        // Validar que el vendedor existe
        String vendedorId = primeraLinea.trim();
        if (!vendedores.containsKey(vendedorId)) {
            reader.close();
            throw new IOException("Vendedor no encontrado: " + vendedorId + " en archivo " + archivo.getName());
        }
        
        String linea;
        boolean ventasEncontradas = false;
        double recaudacionVendedor = recaudacionPorVendedor.get(vendedorId);
        
        while ((linea = reader.readLine()) != null) {
            if (linea.trim().isEmpty()) continue;
            
            String[] partes = linea.split(";");
            if (partes.length >= 2) {
                String idProducto = partes[0].trim();
                
                try {
                    int cantidad = Integer.parseInt(partes[1].trim());
                    
                    if (cantidad < 0) {
                        System.out.println("ADVERTENCIA: Cantidad negativa ignorada en " + archivo.getName() + 
                                         " para producto " + idProducto);
                        continue;
                    }
                    
                    // Validar que el producto existe
                    if (!productos.containsKey(idProducto)) {
                        System.out.println("ADVERTENCIA: Producto inexistente " + idProducto + 
                                         " en archivo " + archivo.getName());
                        continue;
                    }
                    
                    // Actualizar estadísticas
                    ventasPorProducto.put(idProducto, ventasPorProducto.get(idProducto) + cantidad);
                    
                    double precioProducto = productos.get(idProducto).precio;
                    recaudacionVendedor += cantidad * precioProducto;
                    ventasEncontradas = true;
                    
                } catch (NumberFormatException e) {
                    System.out.println("ADVERTENCIA: Cantidad inválida en " + archivo.getName() + ": " + partes[1]);
                }
            }
        }
        
        reader.close();
        
        // Actualizar la recaudación del vendedor
        recaudacionPorVendedor.put(vendedorId, recaudacionVendedor);
        
        return ventasEncontradas;
    }
    
    /**
     * Genera el reporte de vendedores ordenados por recaudación (descendente)
     * 
     * @throws IOException si hay problemas al escribir el archivo
     */
    private static void generarReporteVendedores() throws IOException {
        FileWriter writer = new FileWriter("reporte_vendedores.csv");
        
        try {
            // Crear lista ordenada de vendedores por recaudación
            List<Map.Entry<String, Double>> vendedoresOrdenados = new ArrayList<>(recaudacionPorVendedor.entrySet());
            vendedoresOrdenados.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            
            // Escribir encabezado
            writer.write("Vendedor;Recaudacion\n");
            
            // Escribir datos de vendedores
            for (Map.Entry<String, Double> entrada : vendedoresOrdenados) {
                String vendedorId = entrada.getKey();
                double recaudacion = entrada.getValue();
                
                Vendedor vendedor = vendedores.get(vendedorId);
                if (vendedor != null) {
                    writer.write(vendedor.getNombreCompleto() + ";" + 
                               String.format("%.2f", recaudacion) + "\n");
                }
            }
        } finally {
            writer.close();
        }
    }
    
    /**
     * Genera el reporte de productos ordenados por cantidad vendida (descendente)
     * 
     * @throws IOException si hay problemas al escribir el archivo
     */
    private static void generarReporteProductos() throws IOException {
        FileWriter writer = new FileWriter("reporte_productos.csv");
        
        try {
            // Crear lista ordenada de productos por cantidad vendida
            List<Map.Entry<String, Integer>> productosOrdenados = new ArrayList<>(ventasPorProducto.entrySet());
            productosOrdenados.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
            
            // Escribir encabezado
            writer.write("Producto;Precio;Cantidad_Vendida\n");
            
            // Escribir datos de productos
            for (Map.Entry<String, Integer> entrada : productosOrdenados) {
                String productoId = entrada.getKey();
                int cantidadVendida = entrada.getValue();
                
                Producto producto = productos.get(productoId);
                if (producto != null) {
                    writer.write(producto.nombre + ";" + 
                               String.format("%.2f", producto.precio) + ";" + 
                               cantidadVendida + "\n");
                }
            }
        } finally {
            writer.close();
        }
    }
}