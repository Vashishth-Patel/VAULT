package com.vashishth.vault.Crypto

import android.util.Log

class Crypto {
    fun split(msg : String): List<String> {
        var msg1 : String = ""
        msg.forEach {
            if (it.isWhitespace()){
                msg1+= "_"
            }else{
                msg1+=it
            }
        }
        msg1.replace(" ", "*")
        val size = 16
        val chunks :List<String>
        if (msg1.length >=16 && msg1.length % 16 == 0) {
            chunks = msg1.chunked(size)
        }else if (msg1.length < 16){
            for (i in 0..16-msg.length-1){
                msg1+='_'
            }
            chunks = msg1.chunked(size)
        }else{
            for (i in 0..15 - msg.length % 16 ){
                msg1+='_'
            }
            chunks = msg1.chunked(size)
        }
        return chunks
    }

    fun getMatrix(str16: String,rows:Int): Array<Array<String>> {
        var tempList : List<Char> = emptyList()
        var string = ""
        str16.forEach {
            if (!tempList.contains(it)){
                tempList.plus(it)
                string+=it
            }
        }
        var table = Array(rows) {Array(4) {""} }
        var i =0
        var j =0
        for (k in 0 until string.length){
            if (j > 3){
                j = 0
                i += 1
            }
            table[i][j] = string[k].toString()
            j+=1
        }
        return table
    }


    fun transposeAscii(plainTextArr: Array<Array<String>>, transposeAsciiArr: Array<IntArray>) {
        var i: Int
        var j: Int
        i = 0
        while (i < 4) {
            j = 0
            while (j < 4) {
                transposeAsciiArr[i][j] = plainTextArr[j][i].toCharArray()[0].code
                j++
            }
            i++
        }
    }

    fun GenerateKeyMat(): Array<IntArray> {
        var keyM = Array(4) { IntArray(4) }

        for (i in 0 until 4) {
            for (j in 0 until 4) {
                keyM[i][j] = (33..126).random()
            }
        }
        return keyM
    }

    fun keyMToBinary(keyMatrix:Array<IntArray>): Array<IntArray> {
        var Bkey = Array(4) { IntArray(4) }
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                Bkey[i][j] = keyMatrix[i][j] % 2
            }
        }
        return Bkey
    }

    fun addMatrix(M1:Array<IntArray>,M2:Array<IntArray>):Array<IntArray>{
        var addition:Array<IntArray> = Array(4) { IntArray(4) }
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                addition[i][j] = M1[i][j]+M2[i][j]
            }
        }
        return addition
    }

    fun subtractMatrix(M1: Array<IntArray>, M2: Array<IntArray>):Array<IntArray>{
        var subtract:Array<IntArray> = Array(4) { IntArray(4) }
        for (i in 0 until 4) {
            for (j in 0 until 4) {
                subtract[i][j] = M1[i][j]-M2[i][j]
            }
        }
        return subtract
    }

    fun transposeChar(AsciiMatrix: Array<IntArray>, charArray: Array<CharArray>){
        var i: Int
        var j: Int
        i = 0
        while (i < 4) {
            j = 0
            while (j < 4) {
                charArray[i][j] = AsciiMatrix[j][i].toChar()
                j++
            }
            i++
        }
    }

    fun capsulation(AddedMatrix:Array<IntArray>,Bkey:Array<IntArray>): Array<IntArray> {
        var CapsulationMat = Array(8) { IntArray(4) }
        for (i in 0..1){
            for (j in 0..3){
                CapsulationMat[i][j] = Bkey[i][j]
            }
        }
        for (i in 0..3){
            for (j in 0..3){
                CapsulationMat[i+2][j] = AddedMatrix[i][j]
            }
        }
        for (i in 2..3){
            for (j in 0..3){
                CapsulationMat[i+4][j] = Bkey[i][j]
            }
        }

        return CapsulationMat
    }

    fun shuffling(Arr:Array<IntArray>): Array<IntArray> {
        var shuffledArr = Array(8) { IntArray(4) }
        for(j in 0..3){
            shuffledArr[0][j] = Arr[7][j]
        }
        for (i in 0 until 7){
            for (j in 0 until 4){
                shuffledArr[i+1][j] = Arr[i][j]
            }
        }
        var tempshuffle = Array(8) { IntArray(4) }
        for(i in 0..7){
            tempshuffle[i][0] = shuffledArr[i][3]
        }
        for (j in 0 until 3){
            for (i in 0 until 8){
                tempshuffle[i][j+1] = shuffledArr[i][j]
            }
        }
        return tempshuffle
    }

    fun asciiToChar(Arr: Array<IntArray>): Array<CharArray> {
        var encryptedArr = Array(8) { CharArray(4) }
        for (i in 0 until 8){
            for (j in 0 until 4){
                encryptedArr[i][j] = Arr[i][j].toChar()
            }
        }
        return encryptedArr
    }

    fun charToAscii(Arr: Array<Array<String>>): Array<IntArray> {
        var AsciiArr = Array(8) { IntArray(4) }
        for (i in 0 until 8){
            for (j in 0 until 4){
                AsciiArr[i][j] = Arr[i][j].toCharArray()[0].code
            }
        }
        return AsciiArr
    }

    fun deShuffle(Arr: Array<IntArray>): Array<IntArray> {
        var shuffledArr = Array(8) { IntArray(4) }
        for(i in 0..7){
            shuffledArr[i][3] = Arr[i][0]
        }
        for (j in 0 until 3){
            for (i in 0 until 8){
                shuffledArr[i][j] = Arr[i][j+1]
            }
        }
        var tempshuffle = Array(8) { IntArray(4) }
        for(j in 0..3){
            tempshuffle[7][j] = shuffledArr[0][j]
        }
        for (i in 0 until 7){
            for (j in 0 until 4){
                tempshuffle[i][j] = shuffledArr[i+1][j]
            }
        }
        return tempshuffle
    }

    fun decapsulation(Arr:Array<IntArray>,key:Array<IntArray>,ArrAdd:Array<IntArray>){
        for (i in 0..1){
            for (j in 0..3){
                key[i][j] = Arr[i][j]
            }
        }
        for (i in 0..3){
            for (j in 0..3){
                ArrAdd[i][j] = Arr[i+2][j]
            }
        }
        for (i in 2..3){
            for (j in 0..3){
                key[i][j] = Arr[i+4][j]
            }
        }
    }

    fun encrypt(msg:String) : String{
        var encryptedMsg : String = ""
        var chunks = split(msg = msg)
        chunks.forEach { str16 ->
            var matrix = getMatrix(str16,4)
            var Tmatrix = Array(4) { IntArray(4) }
            transposeAscii(matrix,Tmatrix)
            var keyMat = GenerateKeyMat()
            var Bkey = keyMToBinary(keyMat)
            var addedMatrix = addMatrix(Tmatrix,Bkey)
            var capsulatedMatrix = capsulation(addedMatrix,keyMat)
            var shuffledArr = shuffling(capsulatedMatrix)
            var encryptedArr = asciiToChar(shuffledArr)
            encryptedArr.forEach {
                it.forEach {
                    encryptedMsg+=it
                }
            }
        }
        return encryptedMsg
    }

    fun decrypt(encryptedStr : String) : String{
        var decryptedMsg : String = ""
        var chunks = encryptedStr.chunked(32)
        chunks.forEach { str32 ->
            var encryptedMatrix = getMatrix(str32,8)
            var encrptredAscii = charToAscii(encryptedMatrix)
            var deshuffledMatrix = deShuffle(encrptredAscii)
            var addedMatrix = Array(4) { IntArray(4) }
            var keyMatrix = Array(4) { IntArray(4) }
            decapsulation(Arr = deshuffledMatrix,keyMatrix,addedMatrix)
            var BkeyMatrix = keyMToBinary(keyMatrix)
            var decryptedMatrixAscii = subtractMatrix(addedMatrix,BkeyMatrix)
            var decryptedMatrix = Array(4) { CharArray(4) }
            transposeChar(decryptedMatrixAscii,decryptedMatrix)
            decryptedMatrix.forEach {
                it.forEach {
                    if (it == '_'){
                        decryptedMsg+=' '
                    }else {
                        decryptedMsg += it
                    }
                }
            }
        }
        return decryptedMsg
    }

}