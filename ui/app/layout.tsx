import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Toaster } from "sonner";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "SpectraTest",
  description: "No Code UI Test Automation Framework",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
      >
        <header className="border-b">
          <div className="mx-auto max-w-6xl px-4 h-14 flex items-center justify-between">
            <Link href={"/"} className="font-semibold">
              SpectraTest
            </Link>

            <nav className="flex items-center gap-2">
              <Button variant={"ghost"} asChild>
                <Link href={"/testcases"}>Test Cases</Link>
              </Button>
              <Button asChild>
                <Link href={"/testcases/new"}>New</Link>
              </Button>
            </nav>
          </div>
        </header>
        <main className="mx-auto max-w-6xl px-4 py-6">
          {children}
          <Toaster
            position="top-right"
            richColors
            closeButton
          />
        </main>
      </body>
    </html>
  );
}
