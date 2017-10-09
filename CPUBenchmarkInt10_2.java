import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class CPUBenchmarkInt10_2{
	public static final double runTime=10*60*1e9;
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
		//Int
		System.out.println("\n---------Integer point CPU--------\ntime"+runTime);

		for(int i=0;i<4;i++){
			int num = (int) Math.pow(2, i);
			CPUBenchmarkInt10_2 prog = new CPUBenchmarkInt10_2();
			prog.generateRandAB();
			prog.runBenchmarkInt(num, 3, true);
		}
	}

	public void runBenchmarkInt(int noOfThreads, int runs, boolean enablePrint) throws InterruptedException, ExecutionException {
		IntWorker2[] workers =new IntWorker2[noOfThreads];
		DecimalFormat format = new DecimalFormat("#00.00");
		
		long startTime = System.nanoTime();
		for (int i=0;i<noOfThreads;i++){
			workers[i] = new IntWorker2(this, startTime);
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
			System.out.println("\t Run:"+totalRun+"\t time:"+time+"\tGIOPS:"+format.format(gF));
		//System.out.println("Bye");
	}


	void intCPU(int count) {
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

	class IntWorker2 extends Thread {
		CPUBenchmarkInt10_2 benchmark;
		double runTime = CPUBenchmarkInt10_2.runTime;
		long startTime;

		IntWorker2(CPUBenchmarkInt10_2 cpuBenchmark, long i) {
			this.benchmark = cpuBenchmark;
			this.startTime = i;
		}

		@Override
		public void run() {
			long mins =  System.nanoTime() - startTime;
			while(mins<runTime) {
				benchmark.intCPU(1);
				mins =  System.nanoTime() - startTime;
			}
		}
	}
	class EmptyWorker extends Thread {
		int count;
		CPUBenchmarkInt10_2 benchmark;

		EmptyWorker(CPUBenchmarkInt10_2 cpuBenchmark, int i, int load) {
			this.benchmark = cpuBenchmark;
			this.count = i;
		}
		@Override
		public void run() {
			benchmark.emptyLoop(count);
		}
	}
}
