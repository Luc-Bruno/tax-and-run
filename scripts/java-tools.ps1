function Get-ProjectJavaToolchain {
    param([string]$JavaHome)

    $candidateHomes = New-Object 'System.Collections.Generic.List[string]'
    if ($JavaHome) {
        $candidateHomes.Add($JavaHome)
    } else {
        if ($env:JAVA_HOME) { $candidateHomes.Add($env:JAVA_HOME) }
        $compilerCommand = Get-Command javac.exe -ErrorAction SilentlyContinue
        if ($compilerCommand) {
            $candidateHomes.Add((Split-Path (Split-Path $compilerCommand.Source -Parent) -Parent))
        }
        $personalJdks = Join-Path $env:USERPROFILE '.jdks'
        if (Test-Path -LiteralPath $personalJdks) {
            Get-ChildItem -LiteralPath $personalJdks -Directory |
                Sort-Object Name -Descending |
                ForEach-Object { $candidateHomes.Add($_.FullName) }
        }
    }

    foreach ($candidateHome in $candidateHomes) {
        $compilerPath = Join-Path $candidateHome 'bin\javac.exe'
        $runtimePath = Join-Path $candidateHome 'bin\java.exe'
        if (!(Test-Path -LiteralPath $compilerPath) -or !(Test-Path -LiteralPath $runtimePath)) { continue }
        $versionText = (& $compilerPath -version 2>&1 | Out-String).Trim()
        if ($LASTEXITCODE -eq 0 -and $versionText -match 'javac (\d+)' -and [int]$Matches[1] -ge 17) {
            return [pscustomobject]@{ Javac = $compilerPath; Java = $runtimePath; Version = $versionText }
        }
    }
    throw 'JDK 17+ nao encontrado. Informe -JavaHome com a pasta de um JDK (precisa conter bin\javac.exe).'
}
