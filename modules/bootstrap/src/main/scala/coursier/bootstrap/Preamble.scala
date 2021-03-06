package coursier.bootstrap

object Preamble {

  val argsPartitioner =
    """|nargs=$#
       |
       |i=1; while [ "$i" -le $nargs ]; do
       |         eval arg=\${$i}
       |         case $arg in
       |             -J-*) set -- "$@" "${arg#-J}" ;;
       |         esac
       |         i=$((i + 1))
       |     done
       |
       |set -- "$@" -jar "$0"
       |
       |i=1; while [ "$i" -le $nargs ]; do
       |         eval arg=\${$i}
       |         case $arg in
       |             -J-*) ;;
       |             *) set -- "$@" "$arg" ;;
       |         esac
       |         i=$((i + 1))
       |     done
       |
       |shift "$nargs"
       |""".stripMargin

  def shellPreamble(javaOpts: Seq[String], disableJarChecking: Boolean = false): String = {

    val javaCmd = Seq("java") ++
      {
        if (disableJarChecking)
          Seq("-Dsun.misc.URLClassPath.disableJarChecking")
        else
          Nil
      } ++
      javaOpts
        // escaping possibly a bit loose :-|
        .map(s => "'" + s.replace("'", "\\'") + "'") ++
      Seq("$JAVA_OPTS", "\"$@\"")

    Seq(
      "#!/usr/bin/env sh",
      argsPartitioner,
      "exec " + javaCmd.mkString(" ")
    ).mkString("", "\n", "\n")
  }

}
