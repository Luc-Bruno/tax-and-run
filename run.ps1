param([string]$JavaHome)
$ErrorActionPreference = 'Stop'
& (Join-Path $PSScriptRoot 'build.ps1') -JavaHome $JavaHome
. (Join-Path $PSScriptRoot 'scripts\java-tools.ps1')
$javaToolchain = Get-ProjectJavaToolchain -JavaHome $JavaHome
& $javaToolchain.Java -cp (Join-Path $PSScriptRoot 'out') taxandrun.Main
if ($LASTEXITCODE -ne 0) { throw 'O jogo encerrou com erro.' }
