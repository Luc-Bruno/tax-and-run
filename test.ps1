param([string]$JavaHome, [switch]$WindowSmoke)
$ErrorActionPreference = 'Stop'
& (Join-Path $PSScriptRoot 'build.ps1') -JavaHome $JavaHome
. (Join-Path $PSScriptRoot 'scripts\java-tools.ps1')
$javaToolchain = Get-ProjectJavaToolchain -JavaHome $JavaHome
$buildOutput = Join-Path $PSScriptRoot 'out'
$testSources = @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'test') -Recurse -Filter '*.java' |
    ForEach-Object { $_.FullName })
& $javaToolchain.Javac --release 17 -encoding UTF-8 -Xlint:all -Werror -cp $buildOutput -d $buildOutput @testSources
if ($LASTEXITCODE -ne 0) { throw 'Falha ao compilar testes.' }
Push-Location -LiteralPath $PSScriptRoot
try {
    foreach ($testClass in @('taxandrun.SimulationTest', 'taxandrun.UiTest', 'taxandrun.ArchitectureTest')) {
        & $javaToolchain.Java '-Djava.awt.headless=true' -cp $buildOutput $testClass
        if ($LASTEXITCODE -ne 0) { throw "Falha em $testClass" }
    }
    if ($WindowSmoke) {
        & $javaToolchain.Java -cp $buildOutput taxandrun.WindowSmokeTest
        if ($LASTEXITCODE -ne 0) { throw 'Falha ao verificar a janela Swing.' }
    }
} finally {
    Pop-Location
}
