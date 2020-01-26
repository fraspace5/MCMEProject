MCMEProject Plugin

News
  With this command you can enable or disable the sending of news
  Commands:
    /project news true|false
Teleport
  With this command you can teleport to a warp of a project region
  Commands:
    /project tp <projectName> <projectRegionName>
List
  This command show you all the current ongoing projects that aren't finished or hidden
  Commands:
    /project list [#page]
    /project list [#page] archive (shows also finished or hidden projects)
Projects Info
  This command show you all the information about a project showed (hidden/finished)
  Commands:
    /project details <projectName>
  Information shown:
    Name of the project
    Project Leader
    Project Assistants
    A description of the project
    Current percentage
    Estimated time for the finish of the project
    List of jobs linked to this project
    Link for the forum Thread
    Hours of play
    Amount of players that works on it
    List of region with warps (just click to teleport, if the warp exists)
Project Create+
  With this command you can start a new project. When you create a new project, you will be the leader (You can always change that)
  Commands:
    /project create <projectName>
Project Progress
   This command helps the project leader/ assistants to update the time and the percentage of the project.
   If you are an Artist+ you can update a project only if you are an assistant of this project or the leader
   If you are HeadBuilder you can update all project, even if you aren't a project leader/assistant.
   Everytime you use this command, you send news to all players.
   Commands:
     /project progress <projectName> <newPercentage> <newEstimatedTime>
     For newPercentage and newEstimatedTime you can use " = " if you don't want to change values.
     For newEstimatedTime you need to use a correct language:
     y (year), m (months), w (weeks) d (day).
     You must use only one of them
     Example: If you want to set a time of 1 years and 6 months, use 1.5y
     Example: If you want to set a time of 9 months and 6 days, use 283d
Commands to set all the information of a project
    If you are an Artist+ you can update a project only if you are an assistant of this project or the leader
    If you are HeadBuilder you can update all project, even if you aren't a project leader/assistant.
    Commands:
      /project add <projectName> <playerName>
      Use this command to add an assistant to a project
      /project remove <projectName> <playerName>
      Use this command to remove an assistant from a project
      /project description <projectName>
      Use this command to set the description of a project
      /project head <projectName> <playerName>
      Use this command to change the project leader
      /project name <oldProjectName> <newProjectName>
      Use this command to change the name of the project
      /project time <projectName> <estimatedTime>
      Use this command to change the estimated time for a project (Use the correct Language)
        For newEstimatedTime you need to use a correct language:
        y (year), m (months), w (weeks) d (day).
        You must use only one of them
        Example: If you want to set a time of 1 years and 6 months, use 1.5y
        Example: If you want to set a time of 9 months and 6 days, use 283d
      /project main <projectName>
      Use this command to set a project as the main project of the server.
      ONLY ONE PROJECT CAN BE THE MAIN PROJECT
      /project link <projectName> <link>
      Use this command to set the link for the forum Thread
      /project percentage <projectName> <newPercentage>
      Use this command to change the current percentage
Regions and Warps
    If you are an Artist+ you can update a project only if you are an assistant of this project or the leader
    If you are HeadBuilder you can update all project, even if you aren't a project leader/assistant.
    You need a WE to use the region command
    Commands:
      /project area <projectName> add <regionName>
      /project area <projectName> remove <regionName>
      /project warp <projectName> <regionName>
      You can set ONLY ONE WARP FOR EACH REGION
Project Status
    If you are an Artist+ you can update a project only if you are an assistant of this project or the leader
    If you are HeadBuilder you can update all project, even if you aren't a project leader/assistant.
    Commands:
      /project show <projectName>
      /project hide <projectName>
      /project finish <projectName>
      /project reopen <projectName>