package actor.task

import akka.actor.{Props, ActorLogging, Actor}
import models.task.{TaskCommand, TaskTemplateStep}
import utils.Task_v

/**
 * Created by jinwei on 21/8/14.
 */
class ClusterActor extends Actor with ActorLogging{
  def receive = {
    case gcc: GenerateClusterCommands => {
      context.actorOf(Props(classOf[EngineActor], 3)) ! ReplaceCommand(gcc.taskObj, gcc.templateStep, gcc.cluster)
    }
    case success: SuccessReplaceCommand => {
      sender ! SuccessReplaceCommand(success.commandList)
      context.stop(self)
    }
    case error: ErrorReplaceCommand => {
      sender ! ErrorReplaceCommand(error.keys)
      context.stop(self)
    }
    case gcc: GenerateClusterConfs => {
      context.actorOf(Props(classOf[EngineActor], 15)) ! ReplaceConfigure(gcc.taskObj, gcc.cluster)
    }
    case successConf: SuccessReplaceConf => {
      sender ! successConf
    }
    case errorConf: ErrorReplaceConf => {
      sender ! errorConf
      context.stop(self)
    }
    case timeout: TimeoutReplace => {
      sender ! TimeoutReplace(timeout.key)
      context.stop(self)
    }
    case _ =>
  }
}

case class GenerateClusterCommands(taskId: Int, taskObj: Task_v, templateStep: Seq[TaskTemplateStep], cluster: String)
case class SuccessReplaceCommand(commandList: Seq[TaskCommand])
case class ErrorReplaceCommand(keys: Set[String])

case class GenerateClusterConfs(envId: Int, projectId: Int, versionId: Int, taskObj: Task_v, cluster: String)
case class SuccessReplaceConf(taskId: Int, envId: Int, projectId: Int, versionId: Option[Int])
case class ErrorReplaceConf(str: String)

case class TimeoutReplace(key: String)