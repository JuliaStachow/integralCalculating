import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import static java.lang.Math.ceil;


public class Calka_callable implements Callable<Double>{
//public class Calka_callable{

    private double dx;
    private double xp;
    private double xk;
    private int N;

    private static final int NTHREADS = 4;
    private static final int NZAD = 8;


    public Calka_callable(double xp, double xk, double dx) {
        this.xp = xp;
        this.xk = xk;
        this.N = (int) ceil((xk-xp)/dx);
        this.dx = (xk-xp)/N;
        System.out.println("Creating an instance of Calka_callable");
        System.out.println("xp = " + xp + ", xk = " + xk + ", N = " + N);
        System.out.println("dx requested = " + dx + ", dx final = " + this.dx);
    }

    private double getFunction(double x) {
        return Math.sin(x);
    }

    public double compute_integral() {
        double calka = 0;
        int i;
        for(i=0; i<N; i++){
            double x1 = xp+i*dx;
            double x2 = x1+dx;
            calka += ((getFunction(x1) + getFunction(x2))/2.)*dx;
        }
        System.out.println("Calka czastkowa: " + calka);
        return calka;
    }

    public synchronized Double call() {

        double calka = 0;
        int i;
        for(i=0; i<N; i++){
            double x1 = xp+i*dx;
            double x2 = x1+dx;
            calka += ((getFunction(x1) + getFunction(x2))/2.)*dx;
        }
        System.out.println("Calka czastkowa: " + calka);
        return calka;
    }

    public static void main(String[] args) {
        double xP = 0.0;
        double xK = Math.PI;
        double dX = 0.000001;
        double Nsub = (xK-xP)/NZAD;

        System.out.println("Obliczenia sekwencyjne: \n");

        Calka_callable calka = new Calka_callable(xP,xK,dX);
        calka.compute_integral();

        System.out.println("\nObliczenia rownolegle: \n");

        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        List<Future<Double>> list = new ArrayList<Future<Double>>();

        for(int i=0; i<NZAD; i++){
            Callable<Double> callable = new Calka_callable(xP+i*Nsub, xP+(i+1)*Nsub, dX);
            Future <Double> future = executor.submit(callable);
            list.add(future);
        }

        double calka_g = 0.0;
        for(Future<Double> future_double : list){
            try {
                System.out.println(future_double.get());
                calka_g += future_double.get();
            } catch  (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        System.out.println("\nKoniec obliczen rownoleglych\nObliczona calka: " + calka_g);
    }
}