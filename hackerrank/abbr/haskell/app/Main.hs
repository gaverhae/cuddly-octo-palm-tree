{-# LANGUAGE FlexibleInstances, UndecidableInstances, DuplicateRecordFields, FlexibleContexts, LambdaCase #-}

module Main where

import Control.Monad
import Data.Array
import Data.Bits
import qualified Data.List as List
import Data.List.Split
import Data.Set
import Debug.Trace
import System.Environment
import System.IO
import System.IO.Unsafe
import qualified Data.Char as Char
import qualified Data.Map.Strict as Map
import qualified Data.Set as Set
import qualified Control.Monad.State.Lazy as State

-- Complete the abbreviation function below.
abbreviation :: String -> String -> Bool
abbreviation x y =
  State.evalState (h x y (length x) (length y)) Set.empty
  where h :: String -> String -> Int -> Int -> State.State (Set.Set (Int, Int)) Bool
        h x y dx dy = do
          s <- State.get
          if Set.member (dx, dy) s
          then return False
          else do
            State.modify (Set.insert (dx, dy))
            if dy > dx
            then return False
            else if dy == dx && x == y
            then return True
            else case (x, y, dx, dy) of
                (_, _, 0, 0) -> return True
                (_, _, 0, _) -> return False
                (_, _, _, 0) -> return $ all Char.isLower x
                (a:as, b:bs, dx, dy) | a == b -> h as bs (dx - 1) (dy - 1)
                                     | Char.isUpper a -> return False
                                     | Char.toUpper a /= b -> h as (b:bs) (dx - 1) dy
                                     | otherwise -> h as bs (dx - 1) (dy - 1) >>= \case
                                                      True -> return True
                                                      False -> h as (b:bs) (dx - 1) dy

ab_wrap :: String -> String -> String
ab_wrap a b = if abbreviation a b then "YES" else "NO"

main :: IO()
main = do
  stdout <- getEnv "OUTPUT_PATH"
  fptr <- openFile stdout WriteMode
  q <- readLn :: IO Int
  forM_ [1..q] $ \q_itr -> do
    a <- getLine
    b <- getLine
    let result = ab_wrap a b
    hPutStrLn fptr result
  hFlush fptr
  hClose fptr
