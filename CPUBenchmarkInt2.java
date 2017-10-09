import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class CPUBenchmarkInt2{
	public static final double runTime=1*1e9;
	public double totalRun=0;
	public static final int arrSize=20000;
	public static int a[] = new int[arrSize];
	public static int b[] = new int[arrSize];

	public void generateRandAB() {
		Random random = new Random();
		for (int i = 0; i < arrSize; i++) {
			a[i] = random.nextInt();
			b[i] = random.nextInt();
		}
	}
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		//Float
		System.out.println("\n---------Floating point CPU--------\ntime"+runTime);

		for(int i=0;i<4;i++){
			int num = (int) Math.pow(2, i);
			CPUBenchmarkInt2 prog = new CPUBenchmarkInt2();
			prog.generateRandAB();
			prog.runBenchmarkFloat(num, 3, true);
		}
	}

	public void runBenchmarkFloat(int noOfThreads, int runs, boolean enablePrint) throws InterruptedException, ExecutionException {
		FloatWorker2[] workers =new FloatWorker2[noOfThreads];
		DecimalFormat format = new DecimalFormat("#00.00");
		
		long startTime = System.nanoTime();
		for (int i=0;i<noOfThreads;i++){
			workers[i] = new FloatWorker2(this, startTime);
		}
		//emptyLoop((int)totalRun);
		//Minus time for empty loop
		
		if(enablePrint)
			System.out.print("\nnoOfThreads:"+noOfThreads);
		long start = System.nanoTime();
		for (int i=0;i<noOfThreads;i++){
			workers[i].run();
		}

		for (int i=0;i<noOfThreads;i++){
			workers[i].join();
		}

		long end = System.nanoTime();
		double time = end - start;
		double gF = totalRun/(time);
		
		if(enablePrint)
			System.out.println("\t GFLOPS:"+format.format(gF));
		//System.out.println("Bye");
	}


	void floatCPU(int count) {
		for(int j=0;j<count;j++){
			for(int i=0; i <arrSize; i++){
				a[i] = (a[i] + b[i])*2 + (a[i] * b[i])*3;
				b[i] = (22 + b[i])*2 + (2 * b[i])*2;
			}
		}
		totalRun+=arrSize*10*count+arrSize+count;
	}

	void emptyLoop(int count) {
		for(int j=0;j<count;j++){
			for(int i=0; i <arrSize; i++){

			}
		}
	}

	class FloatWorker2 extends Thread {
		CPUBenchmarkInt2 benchmark;
		double runTime = CPUBenchmarkInt2.runTime;
		long startTime;

		FloatWorker2(CPUBenchmarkInt2 cpuBenchmark, long i) {
			this.benchmark = cpuBenchmark;
			this.startTime = i;
		}

		@Override
		public void run() {
			long mins =  System.nanoTime() - startTime;
			while(mins<runTime) {
				benchmark.floatCPU(1);
				mins =  System.nanoTime() - startTime;
			}
		}
	}
	class EmptyWorker extends Thread {
		int count;
		CPUBenchmarkInt2 benchmark;

		EmptyWorker(CPUBenchmarkInt2 cpuBenchmark, int i, int load) {
			this.benchmark = cpuBenchmark;
			this.count = i;
		}
		@Override
		public void run() {
			benchmark.emptyLoop(count);
		}
	}
}
