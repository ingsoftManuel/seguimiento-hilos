package Banco;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
class Banco {
    private BlockingQueue<Cliente> filaPreferencial;
    private BlockingQueue<Cliente> filaComunitaria;

    public Banco() {
        filaPreferencial = new ArrayBlockingQueue<>(50);
        filaComunitaria = new ArrayBlockingQueue<>(50);
    }

    public void agregarCliente(Cliente cliente) {
        try {
            if (cliente.esPreferencial()) {
                filaPreferencial.put(cliente);
                System.out.println("Cliente preferencial " + cliente.getId() + " en la fila preferencial.");
            } else {
                filaComunitaria.put(cliente);
                System.out.println("Cliente comunitario " + cliente.getId() + " en la fila comunitaria.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Cliente atenderClientePreferencial() throws InterruptedException {
        return filaPreferencial.take();
    }

    public Cliente atenderClienteComunitario() throws InterruptedException {
        return filaComunitaria.take();
    }







}

class Cliente implements Runnable {
    private static int contador = 1;
    private int id;
    private boolean esPreferencial;

    public Cliente(boolean esPreferencial) {
        this.id = contador++;
        this.esPreferencial = esPreferencial;
    }

    public int getId() {
        return id;
    }

    public boolean esPreferencial() {
        return esPreferencial;
    }

    @Override
    public void run() {
        System.out.println("Cliente " + id + " ha llegado al banco.");
    }
}
public class Main {
    public static void main(String[] args) {
        Banco banco = new Banco();

        // Crear clientes y asignarlos a las filas
        for (int i = 0; i < 10; i++) {
            Cliente clientePreferencial = new Cliente(true);
            Cliente clienteComunitario = new Cliente(false);
            banco.agregarCliente(clientePreferencial);
            banco.agregarCliente(clienteComunitario);
        }

        // Atender clientes en paralelo utilizando hilos
        for (int i = 0; i < 10; i++) {
            Thread threadPreferencial = new Thread(() -> {
                try {
                    Cliente cliente = banco.atenderClientePreferencial();
                    System.out.println("Atendiendo cliente preferencial " + cliente.getId());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            Thread threadComunitario = new Thread(() -> {
                try {
                    Cliente cliente = banco.atenderClienteComunitario();
                    System.out.println("Atendiendo cliente comunitario " + cliente.getId());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            threadPreferencial.start();
            threadComunitario.start();
        }
    }
}
