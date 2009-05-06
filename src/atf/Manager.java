package atf;

import ibis.deploy.Application;
import ibis.deploy.ApplicationSet;
import ibis.deploy.Cluster;
import ibis.deploy.Deploy;
import ibis.deploy.Experiment;
import ibis.deploy.Grid;
import ibis.deploy.Job;
import ibis.deploy.JobDescription;
import ibis.deploy.State;
import ibis.deploy.StateListener;
import ibis.deploy.cli.PoolSizePrinter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Manager implements StateListener {

	private double zeta;
	private double delta;
	private int noSampleJobs;

	private ArrayList<Task> jobs;

	public Manager() {
		this.jobs = new ArrayList<Task>();
	}

	public Manager(ArrayList<Task> jobs) {
		this.jobs = jobs;
	}

	public Manager(Task... jobs) {
		this.jobs = new ArrayList<Task>();
		for (Task j : jobs) {
			this.jobs.add(j);
		}
	}

	public boolean execute(File gridFile, long money, long deadline, File home) {
		Grid grid;
		Experiment samplingPhase = null;
		try {
			grid = new Grid(gridFile);
			samplingPhase = new Experiment("SamplingPhase");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			grid = new Grid();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			grid = new Grid();
		}

		if (samplingPhase == null)
			return false;

		zeta = Double.parseDouble(System.getProperty("zeta", "1.96"));
		delta = Double.parseDouble(System.getProperty("delta", "0.15"));
		double zeta_sq = zeta * zeta;
		noSampleJobs = (int) Math.ceil(jobs.size() * zeta_sq
				/ (zeta_sq + 2 * (jobs.size() - 1) * delta * delta));

		ApplicationSet workers = new ApplicationSet();
		File workerLib = new File(System.getProperty("workerlibfile",
				"worker.jar"));

		Application worker = null;
		try {
			worker = workers.createNewApplication("atf-worker");
			worker.setMainClass("atf.Worker");
			worker.setLibs(workerLib);
			worker.checkSettings(worker.getName());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		for (Cluster cluster : grid.getClusters()) {
			// for (int i = 0; i < noSampleJobs; i++) {
			try {
				JobDescription jd = samplingPhase.createNewJob(worker.getName()
						+ "-" + cluster.getName());
				jd.setApplicationName(worker.getName());
				jd.setClusterName(cluster.getName());
				jd.setResourceCount(noSampleJobs);
				jd.setProcessCount(noSampleJobs);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			// }
		}

		Deploy deploy = null;
		try {
			deploy = new Deploy(home, true);
			deploy.initialize(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		for (JobDescription jd : samplingPhase.getJobs()) {
			try {
				deploy.submitJob(jd, workers, grid, this, this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		/*
		System.out.println("Experiment: " + samplingPhase.getName());
		for (Job j : deploy.getJobs()) {
			try {
				System.out.println(j.getDescription().getExperimentName()
						+ "\n" + j.getDescription().toPrintString() + "\n"
						+ j.getState());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 */
		new PoolSizePrinter(deploy);
		try {
			deploy.waitUntilJobsFinished();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void stateUpdated(State newState, Exception exception) {
		// TODO Auto-generated method stub

	}
}
