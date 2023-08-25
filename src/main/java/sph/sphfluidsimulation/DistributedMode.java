package sph.sphfluidsimulation;

import mpi.*;

public class DistributedMode {

    public static void main(String[] args){
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        System.out.println("Hello world from <"+rank+"> of <"+size+">");
        MPI.Finalize();
    }

}
