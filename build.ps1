param([string]$JavaHome)
$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'scripts\java-tools.ps1')
$javaToolchain = Get-ProjectJavaToolchain -JavaHome $JavaHome
$buildOutput = Join-Path $PSScriptRoot 'out'
New-Item -ItemType Directory -Force -Path $buildOutput | Out-Null
# Remove somente bytecode gerado, evitando classes antigas após reorganizar pacotes.
Get-ChildItem -LiteralPath $buildOutput -Recurse -File -Filter '*.class' |
    ForEach-Object { Remove-Item -LiteralPath $_.FullName }
$javaSources = @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'src') -Recurse -Filter '*.java' |
    ForEach-Object { $_.FullName })
& $javaToolchain.Javac --release 17 -encoding UTF-8 -Xlint:all -Werror -d $buildOutput @javaSources
if ($LASTEXITCODE -ne 0) { throw 'Falha na compilacao.' }
Write-Host "Compilado com $($javaToolchain.Version), compativel com Java 17."
