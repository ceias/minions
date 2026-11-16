/** Known-answer regressions for the rubric audit, including larger dynamic datasets. */
import java.util.*;
public class AuditRegressionTests {
 static int pass,fail;
 static void check(String n,boolean ok,String detail){System.out.println((ok?"PASS ":"FAIL ")+n+" | "+detail);if(ok)pass++;else fail++;}
 public static void main(String[] args){
  List<Integer> order=Disk.order(new int[]{50,40},50,"LOOK",100,false);
  check("Disk LOOK down serves request at current head first",order.equals(Arrays.asList(50,40)),"expected [50, 40], actual "+order+"; expected movement 10, actual "+Disk.movement(50,order));
  String b=Banker.run(Arrays.asList("1000000000","P1 1000000000 1000000000","P2 1000000000 1000000000","P3 0 0"),"");
  check("Banker handles accepted large resource totals",b.contains("SYSTEM IS SAFE"),"all three have Need=0; actual "+(b.contains("UNSAFE")?"UNSAFE":"SAFE"));
  String mp=Multi.run(Arrays.asList("A 1 1000000000 0","B 1 1000000000 0","C 1 1000000000 0"),"3","1000000000");
  check("Multiprogramming handles accepted long duration",mp.contains("Total time: 3000000000"),mp.substring(mp.indexOf("Total time:")).replace('\n',';'));
  String frag=Memory.fragmentation(Arrays.asList("50 50","80 200"),"First Fit");
  check("Fragmentation explains which blocked process fits aggregate free memory",!frag.contains("process size(s) [80, 200]"),"free=100; only 80 fits aggregate memory, but reported: "+frag.substring(frag.indexOf("EXTERNAL" )).trim());
  for (String algorithm : new String[]{"SCAN", "C-SCAN", "LOOK", "C-LOOK"}) {
   List<Integer> down=Disk.order(new int[]{50,40,50,60},50,algorithm,100,false);
   check(algorithm+" downward equal-head requests first",down.subList(0,2).equals(Arrays.asList(50,50)),down.toString());
   List<Integer> up=Disk.order(new int[]{50,40,50,60},50,algorithm,100,true);
   check(algorithm+" upward equal-head requests first",up.subList(0,2).equals(Arrays.asList(50,50)),up.toString());
   List<Integer> same=Disk.order(new int[]{50,50},50,algorithm,100,false);
   check(algorithm+" all requests at head need zero movement",same.equals(Arrays.asList(50,50)) && Disk.movement(50,same)==0,same.toString());
  }
  String request=Banker.run(Arrays.asList("1000000000","P1 1000000000 1000000000","P2 1000000000 1000000000","P3 0 1"),"P3 1");
  check("Banker resource-request path uses wide totals",request.contains("GRANTED"),request.substring(request.indexOf("Request")));
  check("Banker unsafe state stays unsafe",Banker.run(Arrays.asList("0","A 1 2","B 1 2"),"").contains("UNSAFE"),"circular unmet unit needs");
  String idle=Multi.run(Arrays.asList("A 1 1 1000000000"),"1","1");
  check("Multiprogramming jumps large idle gap",idle.contains("Total time: 1000000001"),"one job at time1,000,000,000");
  String wideArea=Multi.run(Arrays.asList("A 1000000000 1000000000 0","B 1000000000 1000000000 0","C 1000000000 1000000000 0","D 1000000000 1000000000 0","E 1000000000 1000000000 0","F 1000000000 1000000000 0","G 1000000000 1000000000 0","H 1000000000 1000000000 0","I 1000000000 1000000000 0","J 1000000000 1000000000 0"),"1000000000","1000000000");
  check("Memory-time area larger than long remains valid",wideArea.contains("Average memory utilization: 100.0%")&&wideArea.contains("Total time: 10000000000"),"ten sequential billion-tick jobs");
  check("Fragmentation distinguishes total shortage",!Memory.fragmentation(Arrays.asList("50 50","200"),"First Fit").contains("EXTERNAL FRAGMENTATION:"),"100 total cannot satisfy 200");
  for (String algorithm : new String[]{"C-SCAN", "C-LOOK"})
   check(algorithm+" boundary-head manual movement",Disk.movement(0,Disk.order(new int[]{0,99,50},0,algorithm,100,false))==148,"0 served immediately, then wrap0->99 and descend99->50: 99+49=148");
  int n=128;
  List<String> cpu=new ArrayList<>(),pt=new ArrayList<>(),seg=new ArrayList<>(),jobs=new ArrayList<>(),bank=new ArrayList<>();
  List<String> avail=new ArrayList<>(),zero=new ArrayList<>();
  for(int j=0;j<32;j++){avail.add("1");zero.add("0");}
  bank.add(String.join(" ",avail));
  for(int i=0;i<n;i++){
   cpu.add("P"+i+" 0 1");pt.add(i+" "+2*i);seg.add(i+" "+100*i+" 10");jobs.add("J"+i+" 1 1 0");
   bank.add("P"+i+" "+String.join(" ",zero)+" "+String.join(" ",avail));
  }
  Cpu.Result cr=Cpu.sim(Cpu.parse(cpu),"fcfs",1,0,new int[]{8,16});
  check("CPU 128 dynamic processes",cr.ct[127]==128,"last completion "+cr.ct[127]);
  check("Banker 128 processes and 32 resource types",Banker.run(bank,"").contains("SYSTEM IS SAFE"),"expected safe, zero allocation and unit need");
  check("Paging 128 mapping rows",Memory.paging(pt,"4","508").contains("1016"),"page127 -> frame254, physical1016");
  check("Segmentation 128 segments",Memory.segmentation(seg,"127:9").contains("12709"),"base12700+offset9");
  StringBuilder refs=new StringBuilder();for(int i=0;i<2048;i++)refs.append(i%16).append(' ');
  Pages.Out po=Pages.sim("fifo",Pages.parseRefs(refs.toString()),64,4,4,1);
  check("Replacement 2048 references and 64 frames",po.faults==16&&po.hits==2032,"faults="+po.faults+", hits="+po.hits);
  String blocks=String.join(" ",Collections.nCopies(n,"64")),procs=String.join(" ",Collections.nCopies(n,"32"));
  check("Fragmentation 128 blocks and processes",Memory.fragmentation(Arrays.asList(blocks,procs),"First Fit").contains("Total internal fragmentation: 4096"),"128*(64-32)=4096");
  int[] req=new int[n];for(int i=0;i<n;i++)req[i]=i;
  check("Disk FCFS 128 requests",Disk.movement(0,Disk.order(req,0,"FCFS",200,true))==127,"ascending0..127, expected127");
  String result=Multi.run(jobs,"128","1");
  check("Multiprogramming 128 jobs",result.contains("Total time: 128")&&result.contains("Maximum programs in memory at once: 128"),"expected duration128, peak128");
  System.out.println("Audit regressions: "+pass+" passed, "+fail+" failed; existing test suite results are separate.");
  if (fail > 0) throw new AssertionError(fail + " audit regressions failed");
 }
}
