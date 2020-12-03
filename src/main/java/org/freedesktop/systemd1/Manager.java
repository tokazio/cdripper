package org.freedesktop.systemd1;

import org.freedesktop.dbus.ObjectPath;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;

/**
 * @link https://www.freedesktop.org/software/systemd/man/org.freedesktop.systemd1.html
 */
/*
interface org.freedesktop.systemd1.Manager {
    methods:
      GetUnit(in  s name,
              out o unit);
      GetUnitByPID(in  u pid,
                   out o unit);
      GetUnitByInvocationID(in  ay invocation_id,
                            out o unit);
      GetUnitByControlGroup(in  s cgroup,
                            out o unit);
      LoadUnit(in  s name,
               out o unit);
      StartUnit(in  s name,
                in  s mode,
                out o job);
      StartUnitReplace(in  s old_unit,
                       in  s new_unit,
                       in  s mode,
                       out o job);
      StopUnit(in  s name,
               in  s mode,
               out o job);
      ReloadUnit(in  s name,
                 in  s mode,
                 out o job);
      RestartUnit(in  s name,
                  in  s mode,
                  out o job);
      TryRestartUnit(in  s name,
                     in  s mode,
                     out o job);
      ReloadOrRestartUnit(in  s name,
                          in  s mode,
                          out o job);
      ReloadOrTryRestartUnit(in  s name,
                             in  s mode,
                             out o job);
      EnqueueUnitJob(in  s name,
                     in  s job_type,
                     in  s job_mode,
                     out u job_id,
                     out o job_path,
                     out s unit_id,
                     out o unit_path,
                     out s job_type,
                     out a(uosos) affected_jobs);
      KillUnit(in  s name,
               in  s whom,
               in  i signal);
      CleanUnit(in  s name,
                in  as mask);
      FreezeUnit(in  s name);
      ThawUnit(in  s name);
      ResetFailedUnit(in  s name);
      SetUnitProperties(in  s name,
                        in  b runtime,
                        in  a(sv) properties);
      RefUnit(in  s name);
      UnrefUnit(in  s name);
      StartTransientUnit(in  s name,
                         in  s mode,
                         in  a(sv) properties,
                         in  a(sa(sv)) aux,
                         out o job);
      GetUnitProcesses(in  s name,
                       out a(sus) processes);
      AttachProcessesToUnit(in  s unit_name,
                            in  s subcgroup,
                            in  au pids);
      AbandonScope(in  s name);
      GetJob(in  u id,
             out o job);
      GetJobAfter(in  u id,
                  out a(usssoo) jobs);
      GetJobBefore(in  u id,
                   out a(usssoo) jobs);
      CancelJob(in  u id);
      ClearJobs();
      ResetFailed();
      SetShowStatus(in  s mode);
      ListUnits(out a(ssssssouso) units);
      ListUnitsFiltered(in  as states,
                        out a(ssssssouso) units);
      ListUnitsByPatterns(in  as states,
                          in  as patterns,
                          out a(ssssssouso) units);
      ListUnitsByNames(in  as names,
                       out a(ssssssouso) units);
      ListJobs(out a(usssoo) jobs);
      Subscribe();
      Unsubscribe();
      Dump(out s output);
      DumpByFileDescriptor(out h fd);
      Reload();
      Reexecute();
      Exit();
      Reboot();
      PowerOff();
      Halt();
      KExec();
      SwitchRoot(in  s new_root,
                 in  s init);
      SetEnvironment(in  as assignments);
      UnsetEnvironment(in  as names);
      UnsetAndSetEnvironment(in  as names,
                             in  as assignments);
      ListUnitFiles(out a(ss) unit_files);
      ListUnitFilesByPatterns(in  as states,
                              in  as patterns,
                              out a(ss) unit_files);
      GetUnitFileState(in  s file,
                       out s state);
      EnableUnitFiles(in  as files,
                      in  b runtime,
                      in  b force,
                      out b carries_install_info,
                      out a(sss) changes);
      DisableUnitFiles(in  as files,
                       in  b runtime,
                       out a(sss) changes);
      EnableUnitFilesWithFlags(in  as files,
                               in  t flags,
                               out b carries_install_info,
                               out a(sss) changes);
      DisableUnitFilesWithFlags(in  as files,
                                in  t flags,
                                out a(sss) changes);
      ReenableUnitFiles(in  as files,
                        in  b runtime,
                        in  b force,
                        out b carries_install_info,
                        out a(sss) changes);
      LinkUnitFiles(in  as files,
                    in  b runtime,
                    in  b force,
                    out a(sss) changes);
      PresetUnitFiles(in  as files,
                      in  b runtime,
                      in  b force,
                      out b carries_install_info,
                      out a(sss) changes);
      PresetUnitFilesWithMode(in  as files,
                              in  s mode,
                              in  b runtime,
                              in  b force,
                              out b carries_install_info,
                              out a(sss) changes);
      MaskUnitFiles(in  as files,
                    in  b runtime,
                    in  b force,
                    out a(sss) changes);
      UnmaskUnitFiles(in  as files,
                      in  b runtime,
                      out a(sss) changes);
      RevertUnitFiles(in  as files,
                      out a(sss) changes);
      SetDefaultTarget(in  s name,
                       in  b force,
                       out a(sss) changes);
      GetDefaultTarget(out s name);
      PresetAllUnitFiles(in  s mode,
                         in  b runtime,
                         in  b force,
                         out a(sss) changes);
      AddDependencyUnitFiles(in  as files,
                             in  s target,
                             in  s type,
                             in  b runtime,
                             in  b force,
                             out a(sss) changes);
      GetUnitFileLinks(in  s name,
                       in  b runtime,
                       out as links);
      SetExitCode(in  y number);
      LookupDynamicUserByName(in  s name,
                              out u uid);
      LookupDynamicUserByUID(in  u uid,
                             out s name);
      GetDynamicUsers(out a(us) users);
    signals:
      UnitNew(s id,
              o unit);
      UnitRemoved(s id,
                  o unit);
      JobNew(u id,
             o job,
             s unit);
      JobRemoved(u id,
                 o job,
                 s unit,
                 s result);
      StartupFinished(t firmware,
                      t loader,
                      t kernel,
                      t initrd,
                      t userspace,
                      t total);
      UnitFilesChanged();
      Reloading(b active);
    properties:
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly s Version = '...';
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly s Features = '...';
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly s Virtualization = '...';
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly s Architecture = '...';
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly s Tainted = '...';
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t FirmwareTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t FirmwareTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t LoaderTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t LoaderTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t KernelTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t KernelTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t UserspaceTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t UserspaceTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t FinishTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t FinishTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t SecurityStartTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t SecurityStartTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t SecurityFinishTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t SecurityFinishTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t GeneratorsStartTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t GeneratorsStartTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t GeneratorsFinishTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t GeneratorsFinishTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t UnitsLoadStartTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t UnitsLoadStartTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t UnitsLoadFinishTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t UnitsLoadFinishTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDSecurityStartTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDSecurityStartTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDSecurityFinishTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDSecurityFinishTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDGeneratorsStartTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDGeneratorsStartTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDGeneratorsFinishTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDGeneratorsFinishTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDUnitsLoadStartTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDUnitsLoadStartTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDUnitsLoadFinishTimestamp = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t InitRDUnitsLoadFinishTimestampMonotonic = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      @org.freedesktop.systemd1.Privileged("true")
      readwrite s LogLevel = '...';
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      @org.freedesktop.systemd1.Privileged("true")
      readwrite s LogTarget = '...';
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      readonly u NNames = ...;
      readonly u NFailedUnits = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      readonly u NJobs = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      readonly u NInstalledJobs = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      readonly u NFailedJobs = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      readonly d Progress = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      readonly as Environment = ['...', ...];
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly b ConfirmSpawn = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      readonly b ShowStatus = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly as UnitPath = ['...', ...];
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly s DefaultStandardOutput = '...';
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly s DefaultStandardError = '...';
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      @org.freedesktop.systemd1.Privileged("true")
      readwrite t RuntimeWatchdogUSec = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      @org.freedesktop.systemd1.Privileged("true")
      readwrite t RebootWatchdogUSec = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      @org.freedesktop.systemd1.Privileged("true")
      readwrite t KExecWatchdogUSec = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      @org.freedesktop.systemd1.Privileged("true")
      readwrite b ServiceWatchdogs = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      readonly s ControlGroup = '...';
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      readonly s SystemState = '...';
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      readonly y ExitCode = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultTimerAccuracyUSec = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultTimeoutStartUSec = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultTimeoutStopUSec = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      readonly t DefaultTimeoutAbortUSec = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultRestartUSec = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultStartLimitIntervalUSec = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly u DefaultStartLimitBurst = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly b DefaultCPUAccounting = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly b DefaultBlockIOAccounting = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly b DefaultMemoryAccounting = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly b DefaultTasksAccounting = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitCPU = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitCPUSoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitFSIZE = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitFSIZESoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitDATA = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitDATASoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitSTACK = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitSTACKSoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitCORE = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitCORESoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitRSS = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitRSSSoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitNOFILE = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitNOFILESoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitAS = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitASSoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitNPROC = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitNPROCSoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitMEMLOCK = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitMEMLOCKSoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitLOCKS = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitLOCKSSoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitSIGPENDING = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitSIGPENDINGSoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitMSGQUEUE = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitMSGQUEUESoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitNICE = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitNICESoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitRTPRIO = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitRTPRIOSoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitRTTIME = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t DefaultLimitRTTIMESoft = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("false")
      readonly t DefaultTasksMax = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly t TimerSlackNSec = ...;
      @org.freedesktop.DBus.Property.EmitsChangedSignal("const")
      readonly s DefaultOOMPolicy = '...';
  };
  interface org.freedesktop.DBus.Peer { ... };
  interface org.freedesktop.DBus.Introspectable { ... };
  interface org.freedesktop.DBus.Properties { ... };
 */
public interface Manager extends DBusInterface {

    class UnitNew extends DBusSignal {
        public final String unitName;

        public UnitNew(String unitName, ObjectPath oPath)
                throws DBusException {
            super(unitName, oPath);
            this.unitName = unitName;
        }

        public String getUnitName() {
            return unitName;
        }
    }
}
