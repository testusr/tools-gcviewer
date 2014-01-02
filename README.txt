Visual GC Viewer

visualize key numbers in different time synced charts.
marking the same point in time in all charts with double click markers.

Help to understand and investigate gc related performance problems.

MainClass:
  com.smeo.tools.gc.newparser.gui.VisualGc

argument:
  <gc filename - full path>

Java VM should have following flags enabled:

 * -Xloggc:../../logs/gc.log
 * -XX:+PrintGCApplicationStoppedTime
 * -XX:+PrintGCDetails
 * -XX:+PrintGCTimeStamps
 * -XX:+PrintGCDateStamps
 * -XX:+PrintTenuringDistribution
 *

 Currently supported charts:
 - AllGeneration chart / available used space
 - OldGen chart / available used space
 - YounGen chart / available used space
 - PermGen chart / available used space
 - GcCounts / major minor SystemTriggered
 - Application Stop Time / percentage on one second
 - TenuringAge / newTreshold maxAge
 - Tenuring Space / total[per age] desiredSurvivorSpace
